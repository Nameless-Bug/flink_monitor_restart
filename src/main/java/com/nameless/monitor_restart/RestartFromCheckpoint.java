package com.nameless.monitor_restart;

import com.nameless.monitor_restart.entity.CanceledJob;
import com.nameless.monitor_restart.entity.RunningJob;
import com.nameless.monitor_restart.util.JsonUtil;
import com.nameless.monitor_restart.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestartFromCheckpoint {

    private static final Logger logger = LoggerFactory.getLogger(RestartFromCheckpoint.class);

    public static void main(String[] args) throws IOException {

        final String runningJobsURLAddress = "http://xx.xx.xxx.xxx:8081/jobs/overview";
        final String failedJobsURLAddress = "http://xx.xx.xxx.xxx:8082/jobs/overview";

        String runningJobsInfo = RestUtil.getResponse(runningJobsURLAddress);
        logger.info("succeed in getting running job information from "+runningJobsURLAddress);

//        获取当前正在执行的任务
        List<RunningJob> runningJobList = JsonUtil.getJobList(runningJobsInfo,RunningJob.class);

        List<String> runningJobNames = new ArrayList<>();
        for (RunningJob runningJob:runningJobList){
            if (runningJob.getState().equals("RUNNING")){
                runningJobNames.add(runningJob.getName());
            }
        }

        List<String> monitorNames = new ArrayList<>();
        File f = new File("/xxx.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String monitoredJobName;
        while ((monitoredJobName = br.readLine())!=null){
            monitorNames.add(monitoredJobName);
        }

        monitorNames.removeAll(runningJobNames);
        logger.info("Job(s) "+monitorNames+" are waiting for restarting...");

//        从history server中拿到失败任务并处理
        String canceledJobsInfo = RestUtil.getResponse(failedJobsURLAddress);
        logger.info("succeed in getting failed job information from "+failedJobsURLAddress);

        List<CanceledJob> canceledJobList = JsonUtil.getJobList(canceledJobsInfo,CanceledJob.class);

        Map<String,String> failedJobCheckpoint = new HashMap<>();
        long maxEndtime = 0;
        String jobID = "";

        for (String failedJob:monitorNames){
            for (CanceledJob canceledJob:canceledJobList){
                if (failedJob.equals(canceledJob.getName()) && Long.parseLong(canceledJob.getEndtime())>maxEndtime){
                    jobID = canceledJob.getJid();
                }
            }
            failedJobCheckpoint.put(failedJob,jobID);
        }

        //根据failedJobCheckpoint中的jobid获取checkpoints存储地址
        for (Map.Entry<String,String> entry:failedJobCheckpoint.entrySet()){
            String historyFailedJobURLAddress = "http://xx.xx.xxx.xxx:8082/jobs/"+entry.getValue()+"/checkpoints";
            String historyInfo = RestUtil.getResponse(historyFailedJobURLAddress);
            logger.info("succeed in getting failedjobs' history information from "+historyFailedJobURLAddress);
            String checkpointPath = JsonUtil.getCheckpoint(historyInfo);

            String execStr = "/opt/flink-x.xx.x/bin/flink run -d -p 2 -s "+checkpointPath+" /xxx/sql_job/flink-sql-submit.jar -w /xxx/sql_job/ -f "+entry.getKey()+".sql -n "+entry.getKey();
            logger.info("prepare to execute "+"["+execStr+"]");

            Process process = Runtime.getRuntime().exec(execStr);
            StringBuilder result = new StringBuilder();
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine())!= null){
                    result.append(line);
                }
                logger.info(result.toString());
                int exitCode = process.waitFor();
                logger.info("exitCode = "+exitCode);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
