// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   jobCamel.java

package talendesb.jobcamel_0_1;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;
import routines.*;
import routines.system.*;

public class jobCamel implements routines.system.api.TalendJob
{
    public class ContextProperties extends Properties
    {

        public void synchronizeContext()
        {
            if(filename != null)
                setProperty("filename", filename.toString());
        }

        public String getFilename()
        {
            return filename;
        }

        public String filename;
        final jobCamel this$0;

        public ContextProperties(Properties properties)
        {
            super(properties);
            this$0 = jobCamel.this;
        }

        public ContextProperties()
        {
            super();
            this$0 = jobCamel.this;
        }
    }

    private class TalendException extends Exception
    {

        public void printStackTrace()
        {
            if(!(this.e instanceof TalendException) && !(this.e instanceof TDieException))
            {
                globalMap.put((new StringBuilder(String.valueOf(currentComponent))).append("_ERROR_MESSAGE").toString(), this.e.getMessage());
                System.err.println((new StringBuilder("Exception in component ")).append(currentComponent).toString());
            }
            if(!(this.e instanceof TDieException))
                if(this.e instanceof TalendException)
                {
                    this.e.printStackTrace();
                } else
                {
                    this.e.printStackTrace();
                    this.e.printStackTrace(errorMessagePS);
                    exception = this.e;
                }
            if(!(this.e instanceof TalendException))
                try
                {
                    Method amethod[];
                    int j = (amethod = getClass().getEnclosingClass().getMethods()).length;
                    for(int i = 0; i < j; i++)
                    {
                        Method m = amethod[i];
                        if(m.getName().compareTo((new StringBuilder(String.valueOf(currentComponent))).append("_error").toString()) != 0)
                            continue;
                        m.invoke(jobCamel.this, new Object[] {
                            this.e, currentComponent, globalMap
                        });
                        break;
                    }

                    boolean _tmp = this.e instanceof TDieException;
                }
                catch(SecurityException e)
                {
                    this.e.printStackTrace();
                }
                catch(IllegalArgumentException e)
                {
                    this.e.printStackTrace();
                }
                catch(IllegalAccessException e)
                {
                    this.e.printStackTrace();
                }
                catch(InvocationTargetException e)
                {
                    this.e.printStackTrace();
                }
        }

        private Map globalMap;
        private Exception e;
        private String currentComponent;
        final jobCamel this$0;

        private TalendException(Exception e, String errorComponent, Map globalMap)
        {
            super();
            this$0 = jobCamel.this;
            this.globalMap = null;
            this.e = null;
            currentComponent = null;
            currentComponent = errorComponent;
            this.globalMap = globalMap;
            this.e = e;
        }

        TalendException(Exception exception1, String s, Map map, TalendException talendexception)
        {
            this(exception1, s, map);
        }
    }

    public static class outStruct
        implements IPersistableRow
    {

        public int getId()
        {
            return id;
        }

        public String getName()
        {
            return name;
        }

        public Integer getAge()
        {
            return age;
        }

        private String readString(ObjectInputStream dis)
            throws IOException
        {
            String strReturn = null;
            int length = 0;
            length = dis.readInt();
            if(length == -1)
            {
                strReturn = null;
            } else
            {
                if(length > commonByteArray.length)
                    if(length < 1024 && commonByteArray.length == 0)
                        commonByteArray = new byte[1024];
                    else
                        commonByteArray = new byte[2 * length];
                dis.readFully(commonByteArray, 0, length);
                strReturn = new String(commonByteArray, 0, length, "UTF-8");
            }
            return strReturn;
        }

        private void writeString(String str, ObjectOutputStream dos)
            throws IOException
        {
            if(str == null)
            {
                dos.writeInt(-1);
            } else
            {
                byte byteArray[] = str.getBytes("UTF-8");
                dos.writeInt(byteArray.length);
                dos.write(byteArray);
            }
        }

        private int readInteger(ObjectInputStream dis)
            throws IOException
        {
            int length = 0;
            length = dis.readByte();
            Integer intReturn;
            if(length == -1)
                intReturn = null;
            else
                intReturn = Integer.valueOf(dis.readInt());
            return intReturn.intValue();
        }

        private void writeInteger(Integer intNum, ObjectOutputStream dos)
            throws IOException
        {
            if(intNum == null)
            {
                dos.writeByte(-1);
            } else
            {
                dos.writeByte(0);
                dos.writeInt(intNum.intValue());
            }
        }

        public void readData(ObjectInputStream dis)
        {
            synchronized(commonByteArrayLock)
            {
                try
                {
                    int length = 0;
                    id = dis.readInt();
                    name = readString(dis);
                    age = Integer.valueOf(readInteger(dis));
                }
                catch(IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        public void writeData(ObjectOutputStream dos)
        {
            try
            {
                dos.writeInt(id);
                writeString(name, dos);
                writeInteger(age, dos);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("[");
            sb.append((new StringBuilder("id=")).append(String.valueOf(id)).toString());
            sb.append((new StringBuilder(",name=")).append(name).toString());
            sb.append((new StringBuilder(",age=")).append(String.valueOf(age)).toString());
            sb.append("]");
            return sb.toString();
        }

        public int compareTo(outStruct other)
        {
            int returnValue = -1;
            return returnValue;
        }

        private int checkNullsAndCompare(Object object1, Object object2)
        {
            int returnValue = 0;
            if((object1 instanceof Comparable) && (object2 instanceof Comparable))
                returnValue = ((Comparable)object1).compareTo(object2);
            else
            if(object1 != null && object2 != null)
                returnValue = compareStrings(object1.toString(), object2.toString());
            else
            if(object1 == null && object2 != null)
                returnValue = 1;
            else
            if(object1 != null && object2 == null)
                returnValue = -1;
            else
                returnValue = 0;
            return returnValue;
        }

        private int compareStrings(String string1, String string2)
        {
            return string1.compareTo(string2);
        }

        static final byte commonByteArrayLock[] = new byte[0];
        static byte commonByteArray[] = new byte[0];
        public int id;
        public String name;
        public Integer age;


        public outStruct()
        {
        }
    }

    public static class row1Struct
        implements IPersistableRow
    {

        public int getId()
        {
            return id;
        }

        public String getFirstname()
        {
            return firstname;
        }

        public String getLastname()
        {
            return lastname;
        }

        public Integer getAge()
        {
            return age;
        }

        private String readString(ObjectInputStream dis)
            throws IOException
        {
            String strReturn = null;
            int length = 0;
            length = dis.readInt();
            if(length == -1)
            {
                strReturn = null;
            } else
            {
                if(length > commonByteArray.length)
                    if(length < 1024 && commonByteArray.length == 0)
                        commonByteArray = new byte[1024];
                    else
                        commonByteArray = new byte[2 * length];
                dis.readFully(commonByteArray, 0, length);
                strReturn = new String(commonByteArray, 0, length, "UTF-8");
            }
            return strReturn;
        }

        private void writeString(String str, ObjectOutputStream dos)
            throws IOException
        {
            if(str == null)
            {
                dos.writeInt(-1);
            } else
            {
                byte byteArray[] = str.getBytes("UTF-8");
                dos.writeInt(byteArray.length);
                dos.write(byteArray);
            }
        }

        private int readInteger(ObjectInputStream dis)
            throws IOException
        {
            int length = 0;
            length = dis.readByte();
            Integer intReturn;
            if(length == -1)
                intReturn = null;
            else
                intReturn = Integer.valueOf(dis.readInt());
            return intReturn.intValue();
        }

        private void writeInteger(Integer intNum, ObjectOutputStream dos)
            throws IOException
        {
            if(intNum == null)
            {
                dos.writeByte(-1);
            } else
            {
                dos.writeByte(0);
                dos.writeInt(intNum.intValue());
            }
        }

        public void readData(ObjectInputStream dis)
        {
            synchronized(commonByteArrayLock)
            {
                try
                {
                    int length = 0;
                    id = dis.readInt();
                    firstname = readString(dis);
                    lastname = readString(dis);
                    age = Integer.valueOf(readInteger(dis));
                }
                catch(IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        public void writeData(ObjectOutputStream dos)
        {
            try
            {
                dos.writeInt(id);
                writeString(firstname, dos);
                writeString(lastname, dos);
                writeInteger(age, dos);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            sb.append("[");
            sb.append((new StringBuilder("id=")).append(String.valueOf(id)).toString());
            sb.append((new StringBuilder(",firstname=")).append(firstname).toString());
            sb.append((new StringBuilder(",lastname=")).append(lastname).toString());
            sb.append((new StringBuilder(",age=")).append(String.valueOf(age)).toString());
            sb.append("]");
            return sb.toString();
        }

        public int compareTo(row1Struct other)
        {
            int returnValue = -1;
            return returnValue;
        }

        private int checkNullsAndCompare(Object object1, Object object2)
        {
            int returnValue = 0;
            if((object1 instanceof Comparable) && (object2 instanceof Comparable))
                returnValue = ((Comparable)object1).compareTo(object2);
            else
            if(object1 != null && object2 != null)
                returnValue = compareStrings(object1.toString(), object2.toString());
            else
            if(object1 == null && object2 != null)
                returnValue = 1;
            else
            if(object1 != null && object2 == null)
                returnValue = -1;
            else
                returnValue = 0;
            return returnValue;
        }

        private int compareStrings(String string1, String string2)
        {
            return string1.compareTo(string2);
        }

        static final byte commonByteArrayLock[] = new byte[0];
        static byte commonByteArray[] = new byte[0];
        public int id;
        public String firstname;
        public String lastname;
        public Integer age;


        public row1Struct()
        {
        }
    }


    public jobCamel()
    {
        valueObject = null;
        defaultProps = new Properties();
        context = new ContextProperties();
        errorCode = null;
        currentComponent = "";
        errorMessagePS = new PrintStream(new BufferedOutputStream(baos));
        exception = null;
        resuming_logs_dir_path = null;
        resuming_checkpoint_path = null;
        parent_part_launcher = null;
        resumeEntryMethodName = null;
        globalResumeTicket = false;
        watch = false;
        portStats = null;
        portTraces = 4334;
        defaultClientHost = "localhost";
        contextStr = "Default";
        pid = "0";
        rootPid = null;
        fatherPid = null;
        fatherNode = null;
        startTime = 0L;
        isChildJob = false;
        execStat = true;
        threadLocal = new ThreadLocal();
        Map threadRunResultMap = new HashMap();
        threadRunResultMap.put("errorCode", null);
        threadRunResultMap.put("status", "");
        threadLocal.set(threadRunResultMap);
        context_param = new Properties();
        parentContextMap = new HashMap();
        status = "";
        resumeUtil = null;
    }

    public Object getValueObject()
    {
        return valueObject;
    }

    public void setValueObject(Object valueObject)
    {
        this.valueObject = valueObject;
    }

    public ContextProperties getContext()
    {
        return context;
    }

    public String getExceptionStackTrace()
    {
        if("failure".equals(getStatus()))
        {
            errorMessagePS.flush();
            return baos.toString();
        } else
        {
            return null;
        }
    }

    public Exception getException()
    {
        if("failure".equals(getStatus()))
            return exception;
        else
            return null;
    }

    public void tRowGenerator_1_error(Exception exception, String errorComponent, Map globalMap)
        throws TalendException
    {
        end_Hash.put("tRowGenerator_1", Long.valueOf(System.currentTimeMillis()));
        tRowGenerator_1_onSubJobError(exception, errorComponent, globalMap);
    }

    public void tMap_1_error(Exception exception, String errorComponent, Map globalMap)
        throws TalendException
    {
        end_Hash.put("tMap_1", Long.valueOf(System.currentTimeMillis()));
        tRowGenerator_1_onSubJobError(exception, errorComponent, globalMap);
    }

    public void tFileOutputDelimited_1_error(Exception exception, String errorComponent, Map globalMap)
        throws TalendException
    {
        end_Hash.put("tFileOutputDelimited_1", Long.valueOf(System.currentTimeMillis()));
        tRowGenerator_1_onSubJobError(exception, errorComponent, globalMap);
    }

    public void tRowGenerator_1_onSubJobError(Exception exception, String errorComponent, Map globalMap)
        throws TalendException
    {
        resumeUtil.addLog("SYSTEM_LOG", (new StringBuilder("NODE:")).append(errorComponent).toString(), "", (new StringBuilder(String.valueOf(Thread.currentThread().getId()))).toString(), "FATAL", "", exception.getMessage(), ResumeUtil.getExceptionStackTrace(exception), "");
    }

    public void tRowGenerator_1Process(Map globalMap)
        throws TalendException
    {
        globalMap.put("tRowGenerator_1_SUBPROCESS_STATE", Integer.valueOf(0));
        boolean execStat = this.execStat;
        String iterateId = "";
        String currentComponent = "";
        try
        {
            String currentMethodName = (new Exception()).getStackTrace()[0].getMethodName();
            boolean resumeIt = currentMethodName.equals(resumeEntryMethodName);
            if(resumeEntryMethodName == null || resumeIt || globalResumeTicket)
            {
                globalResumeTicket = true;
                row1Struct row1 = new row1Struct();
                outStruct out = new outStruct();
                ok_Hash.put("tFileOutputDelimited_1", Boolean.valueOf(false));
                start_Hash.put("tFileOutputDelimited_1", Long.valueOf(System.currentTimeMillis()));
                currentComponent = "tFileOutputDelimited_1";
                int tos_count_tFileOutputDelimited_1 = 0;
                String fileName_tFileOutputDelimited_1 = (new File(context.filename)).getAbsolutePath().replace("\\", "/");
                String fullName_tFileOutputDelimited_1 = null;
                String extension_tFileOutputDelimited_1 = null;
                String directory_tFileOutputDelimited_1 = null;
                if(fileName_tFileOutputDelimited_1.indexOf("/") != -1)
                {
                    if(fileName_tFileOutputDelimited_1.lastIndexOf(".") < fileName_tFileOutputDelimited_1.lastIndexOf("/"))
                    {
                        fullName_tFileOutputDelimited_1 = fileName_tFileOutputDelimited_1;
                        extension_tFileOutputDelimited_1 = "";
                    } else
                    {
                        fullName_tFileOutputDelimited_1 = fileName_tFileOutputDelimited_1.substring(0, fileName_tFileOutputDelimited_1.lastIndexOf("."));
                        extension_tFileOutputDelimited_1 = fileName_tFileOutputDelimited_1.substring(fileName_tFileOutputDelimited_1.lastIndexOf("."));
                    }
                    directory_tFileOutputDelimited_1 = fileName_tFileOutputDelimited_1.substring(0, fileName_tFileOutputDelimited_1.lastIndexOf("/"));
                } else
                {
                    if(fileName_tFileOutputDelimited_1.lastIndexOf(".") != -1)
                    {
                        fullName_tFileOutputDelimited_1 = fileName_tFileOutputDelimited_1.substring(0, fileName_tFileOutputDelimited_1.lastIndexOf("."));
                        extension_tFileOutputDelimited_1 = fileName_tFileOutputDelimited_1.substring(fileName_tFileOutputDelimited_1.lastIndexOf("."));
                    } else
                    {
                        fullName_tFileOutputDelimited_1 = fileName_tFileOutputDelimited_1;
                        extension_tFileOutputDelimited_1 = "";
                    }
                    directory_tFileOutputDelimited_1 = "";
                }
                boolean isFileGenerated_tFileOutputDelimited_1 = true;
                int nb_line_tFileOutputDelimited_1 = 0;
                int splitEvery_tFileOutputDelimited_1 = 1000;
                int splitedFileNo_tFileOutputDelimited_1 = 0;
                int currentRow_tFileOutputDelimited_1 = 0;
                String OUT_DELIM_tFileOutputDelimited_1 = ";";
                String OUT_DELIM_ROWSEP_tFileOutputDelimited_1 = "\n";
                if(directory_tFileOutputDelimited_1 != null && directory_tFileOutputDelimited_1.trim().length() != 0)
                {
                    File dir_tFileOutputDelimited_1 = new File(directory_tFileOutputDelimited_1);
                    if(!dir_tFileOutputDelimited_1.exists())
                        dir_tFileOutputDelimited_1.mkdirs();
                }
                Writer outtFileOutputDelimited_1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName_tFileOutputDelimited_1, false), "ISO-8859-15"));
                File filetFileOutputDelimited_1 = new File(fileName_tFileOutputDelimited_1);
                ok_Hash.put("tMap_1", Boolean.valueOf(false));
                start_Hash.put("tMap_1", Long.valueOf(System.currentTimeMillis()));
                currentComponent = "tMap_1";
                int tos_count_tMap_1 = 0;
                outStruct out_tmp = new outStruct();
                ok_Hash.put("tRowGenerator_1", Boolean.valueOf(false));
                start_Hash.put("tRowGenerator_1", Long.valueOf(System.currentTimeMillis()));
                currentComponent = "tRowGenerator_1";
                int tos_count_tRowGenerator_1 = 0;
                int nb_line_tRowGenerator_1 = 0;
                int nb_max_row_tRowGenerator_1 = 100;
                class _cls1tRowGenerator_1Randomizer
                {

                    public int getRandomid()
                    {
                        return Numeric.sequence("s1", 1, 1).intValue();
                    }

                    public String getRandomfirstname()
                    {
                        return TalendDataGenerator.getFirstName();
                    }

                    public String getRandomlastname()
                    {
                        return TalendDataGenerator.getLastName();
                    }

                    public Integer getRandomage()
                    {
                        return Numeric.random(Integer.valueOf(0), Integer.valueOf(100));
                    }

                    final jobCamel this$0;

            _cls1tRowGenerator_1Randomizer()
            {
                super();
                this$0 = jobCamel.this;
            }
                }

                _cls1tRowGenerator_1Randomizer randtRowGenerator_1 = new _cls1tRowGenerator_1Randomizer();
                for(int itRowGenerator_1 = 0; itRowGenerator_1 < nb_max_row_tRowGenerator_1; itRowGenerator_1++)
                {
                    row1.id = randtRowGenerator_1.getRandomid();
                    row1.firstname = randtRowGenerator_1.getRandomfirstname();
                    row1.lastname = randtRowGenerator_1.getRandomlastname();
                    row1.age = randtRowGenerator_1.getRandomage();
                    nb_line_tRowGenerator_1++;
                    currentComponent = "tRowGenerator_1";
                    tos_count_tRowGenerator_1++;
                    currentComponent = "tMap_1";
                    boolean hasCasePrimitiveKeyWithNull_tMap_1 = false;
                    boolean rejectedInnerJoin_tMap_1 = false;
                    boolean mainRowRejected_tMap_1 = false;
                    out = null;
                    out_tmp.id = row1.id;
                    out_tmp.name = (new StringBuilder(String.valueOf(row1.firstname))).append(" ").append(row1.lastname).toString();
                    out_tmp.age = row1.age;
                    out = out_tmp;
                    rejectedInnerJoin_tMap_1 = false;
                    tos_count_tMap_1++;
                    if(out != null)
                    {
                        currentComponent = "tFileOutputDelimited_1";
                        StringBuilder sb_tFileOutputDelimited_1 = new StringBuilder();
                        sb_tFileOutputDelimited_1.append(out.id);
                        sb_tFileOutputDelimited_1.append(";");
                        if(out.name != null)
                            sb_tFileOutputDelimited_1.append(out.name);
                        sb_tFileOutputDelimited_1.append(";");
                        if(out.age != null)
                            sb_tFileOutputDelimited_1.append(out.age);
                        sb_tFileOutputDelimited_1.append("\n");
                        nb_line_tFileOutputDelimited_1++;
                        outtFileOutputDelimited_1.write(sb_tFileOutputDelimited_1.toString());
                        tos_count_tFileOutputDelimited_1++;
                    }
                    currentComponent = "tRowGenerator_1";
                }

                globalMap.put("tRowGenerator_1_NB_LINE", Integer.valueOf(nb_line_tRowGenerator_1));
                ok_Hash.put("tRowGenerator_1", Boolean.valueOf(true));
                end_Hash.put("tRowGenerator_1", Long.valueOf(System.currentTimeMillis()));
                currentComponent = "tMap_1";
                ok_Hash.put("tMap_1", Boolean.valueOf(true));
                end_Hash.put("tMap_1", Long.valueOf(System.currentTimeMillis()));
                currentComponent = "tFileOutputDelimited_1";
                outtFileOutputDelimited_1.close();
                globalMap.put("tFileOutputDelimited_1_NB_LINE", Integer.valueOf(nb_line_tFileOutputDelimited_1));
                ok_Hash.put("tFileOutputDelimited_1", Boolean.valueOf(true));
                end_Hash.put("tFileOutputDelimited_1", Long.valueOf(System.currentTimeMillis()));
            }
        }
        catch(Exception e)
        {
            throw new TalendException(e, currentComponent, globalMap, null);
        }
        catch(Error error)
        {
            throw new Error(error);
        }
        globalMap.put("tRowGenerator_1_SUBPROCESS_STATE", Integer.valueOf(1));
    }

    public static void main(String args[])
    {
        jobCamel jobCamelClass = new jobCamel();
        int exitCode = jobCamelClass.runJobInTOS(args);
        System.exit(exitCode);
    }

    public String[][] runJob(String args[])
    {
        int exitCode = runJobInTOS(args);
        String bufferValue[][] = {
            {
                Integer.toString(exitCode)
            }
        };
        return bufferValue;
    }

    public int runJobInTOS(String args[])
    {
        String lastStr = "";
        boolean hasContextArg = false;
        String as[];
        long endUsedMemory = (as = args).length;
        for(int i = 0; i < endUsedMemory; i++)
        {
            String arg = as[i];
            if(arg.toLowerCase().contains("--context="))
                hasContextArg = true;
            else
            if(arg.equalsIgnoreCase("--context_param"))
                lastStr = arg;
            else
            if(lastStr.equals(""))
            {
                evalParam(arg);
            } else
            {
                evalParam((new StringBuilder(String.valueOf(lastStr))).append(" ").append(arg).toString());
                lastStr = "";
            }
        }

        if(clientHost == null)
            clientHost = defaultClientHost;
        if(pid == null || "0".equals(pid))
            pid = TalendString.getAsciiRandomString(6);
        if(rootPid == null)
            rootPid = pid;
        if(fatherPid == null)
            fatherPid = pid;
        else
            isChildJob = true;
        try
        {
            InputStream inContext = jobCamel.class.getClassLoader().getResourceAsStream((new StringBuilder("talendesb/jobcamel_0_1/contexts/")).append(contextStr).append(".properties").toString());
            if(inContext != null)
            {
                defaultProps.load(inContext);
                inContext.close();
                context = new ContextProperties(defaultProps);
            } else
            if(hasContextArg)
                System.err.println((new StringBuilder("Could not find the context ")).append(contextStr).toString());
            if(!context_param.isEmpty())
                context.putAll(context_param);
            context.filename = context.getProperty("filename");
        }
        catch(IOException ie)
        {
            System.err.println((new StringBuilder("Could not load context ")).append(contextStr).toString());
            ie.printStackTrace();
        }
        if(parentContextMap != null && !parentContextMap.isEmpty() && parentContextMap.containsKey("filename"))
            context.filename = (String)parentContextMap.get("filename");
        resumeEntryMethodName = ResumeUtil.getResumeEntryMethodName(resuming_checkpoint_path);
        resumeUtil = new ResumeUtil(resuming_logs_dir_path, isChildJob, rootPid);
        resumeUtil.initCommonInfo(pid, rootPid, fatherPid, "TALENDESB", "jobCamel", contextStr, "0.1");
        resumeUtil.addLog("JOB_STARTED", "JOB:jobCamel", parent_part_launcher, (new StringBuilder(String.valueOf(Thread.currentThread().getId()))).toString(), "", "", "", "", ResumeUtil.convertToJsonText(context));
        long startUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        endUsedMemory = 0L;
        long end = 0L;
        startTime = System.currentTimeMillis();
        globalResumeTicket = true;
        globalResumeTicket = false;
        try
        {
            errorCode = null;
            tRowGenerator_1Process(globalMap);
            status = "end";
        }
        catch(TalendException e_tRowGenerator_1)
        {
            status = "failure";
            e_tRowGenerator_1.printStackTrace();
            globalMap.put("tRowGenerator_1_SUBPROCESS_STATE", Integer.valueOf(-1));
        }
        globalResumeTicket = true;
        end = System.currentTimeMillis();
        if(watch)
            System.out.println((new StringBuilder(String.valueOf(end - startTime))).append(" milliseconds").toString());
        endUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int returnCode = 0;
        if(errorCode == null)
            returnCode = status == null || !status.equals("failure") ? 0 : 1;
        else
            returnCode = errorCode.intValue();
        resumeUtil.addLog("JOB_ENDED", "JOB:jobCamel", parent_part_launcher, (new StringBuilder(String.valueOf(Thread.currentThread().getId()))).toString(), "", (new StringBuilder()).append(returnCode).toString(), "", "", "");
        return returnCode;
    }

    private void evalParam(String arg)
    {
        if(arg.startsWith("--resuming_logs_dir_path"))
            resuming_logs_dir_path = arg.substring(25);
        else
        if(arg.startsWith("--resuming_checkpoint_path"))
            resuming_checkpoint_path = arg.substring(27);
        else
        if(arg.startsWith("--parent_part_launcher"))
            parent_part_launcher = arg.substring(23);
        else
        if(arg.startsWith("--watch"))
            watch = true;
        else
        if(arg.startsWith("--stat_port="))
        {
            String portStatsStr = arg.substring(12);
            if(portStatsStr != null && !portStatsStr.equals("null"))
                portStats = Integer.valueOf(Integer.parseInt(portStatsStr));
        } else
        if(arg.startsWith("--trace_port="))
            portTraces = Integer.parseInt(arg.substring(13));
        else
        if(arg.startsWith("--client_host="))
            clientHost = arg.substring(14);
        else
        if(arg.startsWith("--context="))
            contextStr = arg.substring(10);
        else
        if(arg.startsWith("--father_pid="))
            fatherPid = arg.substring(13);
        else
        if(arg.startsWith("--root_pid="))
            rootPid = arg.substring(11);
        else
        if(arg.startsWith("--father_node="))
            fatherNode = arg.substring(14);
        else
        if(arg.startsWith("--pid="))
            pid = arg.substring(6);
        else
        if(arg.startsWith("--context_param"))
        {
            String keyValue = arg.substring(16);
            int index = -1;
            if(keyValue != null && (index = keyValue.indexOf('=')) > -1)
                context_param.put(keyValue.substring(0, index), keyValue.substring(index + 1));
        }
    }

    public Integer getErrorCode()
    {
        return errorCode;
    }

    public String getStatus()
    {
        return status;
    }

    public final Object obj = new Object();
    private Object valueObject;
    private static final String defaultCharset = Charset.defaultCharset().name();
    private static final String utf8Charset = "UTF-8";
    private Properties defaultProps;
    private ContextProperties context;
    private final String jobVersion = "0.1";
    private final String jobName = "jobCamel";
    private final String projectName = "TALENDESB";
    public Integer errorCode;
    private String currentComponent;
    private final Map start_Hash = new HashMap();
    private final Map end_Hash = new HashMap();
    private final Map ok_Hash = new HashMap();
    private final Map globalMap = new HashMap();
    public final List globalBuffer = new ArrayList();
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final PrintStream errorMessagePS;
    private Exception exception;
    public String resuming_logs_dir_path;
    public String resuming_checkpoint_path;
    public String parent_part_launcher;
    private String resumeEntryMethodName;
    private boolean globalResumeTicket;
    public boolean watch;
    public Integer portStats;
    public int portTraces;
    public String clientHost;
    public String defaultClientHost;
    public String contextStr;
    public String pid;
    public String rootPid;
    public String fatherPid;
    public String fatherNode;
    public long startTime;
    public boolean isChildJob;
    private boolean execStat;
    private ThreadLocal threadLocal;
    private Properties context_param;
    public Map parentContextMap;
    public String status;
    ResumeUtil resumeUtil;



}
