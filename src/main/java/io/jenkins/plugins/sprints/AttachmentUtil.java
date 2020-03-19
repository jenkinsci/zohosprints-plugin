package io.jenkins.plugins.sprints;

import hudson.model.Run;
import hudson.model.TaskListener;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *@author selvavignesh.m
 * @version 1.0
 */
public class AttachmentUtil {

    private Run<?, ?> run;
    private TaskListener tl;
    private static final String DATA_SOURCE_NAME = "build.log";
    private static final Logger LOGGER = Logger.getLogger(AttachmentUtil.class.getName());

    /**
     *
     * @param runObj Run Object of Build
     * @param listener listener Receives events that happen during a build
     */
    public AttachmentUtil(final Run<?, ?> runObj, TaskListener listener) {
        this.run = runObj;
        this.tl = listener;
    }

    /**
     *
     * @return Input Stream of File
     */
    public InputStream getInputStream() {
        InputStream res;
        long logFileLength = run.getLogText().length();
        long pos = 0;
        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        while (pos < logFileLength) {
            try {
                pos = run.getLogText().writeLogTo(pos, bao);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "", e);
            }
        }

       // res = new ByteArrayInputStream(bao.toByteArray());

        try {
            return getLogFileContennt(new ByteArrayInputStream(bao.toByteArray()));
        } catch (IOException e) {
           LOGGER.log(Level.WARNING, "", e);
        }
        return null;
    }

    /**
     *
     * @return throws IOException
     * @throws IOException unsuported operation
     */
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Unsupported");
    }

    /**
     *
     * @return Contnet type
     */
    public String getContentType() {
        return "text/plain";
    }

    /**
     *
     * @return Name of the file Name
     */
    public String getName() {
        return DATA_SOURCE_NAME;
    }

    /**
     *
     * @return Size of the File
     */
    public long getSize() {
        return run.getLogText().length();
    }

    /**
     *
     * @param stream InputStream of the Log file
     * @return InputStream of file
     * @throws IOException Input/Output error
     */
    public InputStream getLogFileContennt(InputStream stream) throws IOException {
        StringBuilder logContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")))) {
            // br = new BufferedReader(new InputStreamReader(getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                logContent.append(line);
                logContent.append("<br>");
            }

            return new ByteArrayInputStream(logContent.toString().getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw e;
        }

    }

    /**
     *
     * @param filename Filename of the log file
     * @return part of file
     * @throws IOException Input/Output error
     */
    public Part[] getLogAttachment(final String filename) throws IOException {

        try (InputStream ip = getInputStream()) {
            byte[] byteArr = IOUtils.toByteArray(ip);
            ByteArrayPartSource bytePartArray = new ByteArrayPartSource(filename, byteArr);
            Part[] partArr = {
                    new FilePart("MULTIPART_FORM_REQUEST", bytePartArray)
            };
            return partArr;
        } catch (IOException e) {
            tl.error("Error in getting log attachment");
            throw e;
        }
    }
}
