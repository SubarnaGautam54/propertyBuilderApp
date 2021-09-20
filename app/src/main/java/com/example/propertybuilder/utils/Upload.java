package com.example.propertybuilder.utils;

import android.util.Log;

import com.example.propertybuilder.ConstantApis.Api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Belal on 11/22/2015.
 */
public class Upload {


    private int serverResponseCode;

    public String upLoad2Server(String sourceFileUri) {
        Log.d("wow123", "upLoad2Server: Call");
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            Log.e("wow123", "Source File Does not exist");
            return "Source File Does not exist";
        }
        else
        {
            Log.d("wow123", "upLoad2Server: file exist");

        }

        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(Api.UPLOAD_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("myFile", fileName);
//            conn.setRequestProperty("User-Agent", "CodeJava Agent");
//            conn.setRequestProperty("id", "Bonjour");
//            conn.setRequestProperty("id", id);
            conn.setRequestProperty("id", "111");
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            Log.i("wow123", "Initial .available : " + bytesAvailable);

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                Log.d("wow123", "upLoad2Server: file exist"+bytesRead);

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();

            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("wow123", "upLoad2Server: Exception");
        }

//        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
//                    sb = line;
                    Log.d("wow123", "upLoad2Server: "+line);
                }
                rd.close();
            } catch (IOException ioex) {
                Log.d("wow123", "upLoad2Server: IOException"+ioex.getMessage());
            }
            return sb.toString();
//        }else {
//            return "Could not upload";
//        }
    }
}
