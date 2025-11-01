package club.beenest.quartz.util;

import org.quartz.JobExecutionContext;
import club.beenest.quartz.domain.SysJob;

/**
 * 定时任务处理（允许并发执行）
 * 
 * @author beenest
 *
 */
public class QuartzJobExecution extends AbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception
    {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
