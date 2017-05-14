package com.thangld.managechildren.cloud;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by thangld on 10/04/2017.
 */

public class HttpConnection {

    public static String exePostConnection(URL url, JSONObject jsonParams) {
        HttpURLConnection conn = null;
        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            if (jsonParams != null) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParams.toString());
                writer.flush();
                writer.close();
                os.close();
            }
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d("mc_log", "responseCode" + responseCode);
            String response = "";
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {

                response = "{\"status\":0, \"msg\":\"unknown\"}";
//                String line;
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                while ((line = br.readLine()) != null) {
//                    response += line;
//                }
            }
            Log.d("mc_log", "response:" + response);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String exeGetConnection(String urlStr, HashMap<String, String> params) {
        HttpURLConnection conn = null;
        try {
            urlStr = createUrlGet(urlStr, params);
            Log.d("mc_log", "exeGetConnection url:" + urlStr);
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d("mc_log", "responseCode" + responseCode);
            String response = "";
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "{\"status\":0, \"msg\":\"unknown\"}";

            }
            Log.d("mc_log", "response:" + response);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String exePutConnection(URL url, JSONObject jsonParams) {
        HttpURLConnection conn = null;
        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            if (jsonParams != null) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParams.toString());
                writer.flush();
                writer.close();
                os.close();
            }
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d("mc_log", "responseCode" + responseCode);
            String response = "";
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {

                response = "{\"status\":0, \"msg\":\"unknown\"}";
//                String line;
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                while ((line = br.readLine()) != null) {
//                    response += line;
//                }
            }
            Log.d("mc_log", "response:" + response);
            return response;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean downloadFile(String urlStr, JSONObject jsonParams, String filePath, String fileName) {
        Log.d("mc_log", "download()");
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            if (jsonParams != null) {
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParams.toString());
                writer.flush();
                writer.close();
                os.close();
            }
            connection.connect();
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            } else {
                input = connection.getInputStream();
                output = new FileOutputStream(filePath + File.separator + fileName);
                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                return true;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return false;
    }

    /**
     * Chỉ dùng cho gửi file
     *
     * @param connectURL
     * @return
     */
    @NonNull
    public static String exePutFileConnection(URL connectURL, String pathFile, HashMap<String, String> infoAuth) {
        /**
         * Json gửi đến có dữ liệu
         * {
         *      "file_path":String
         *     "display_name": "abc",
         *     "size": 12,
         *     "description": "sdsd",
         *     "sha1": "wertyuiop",
         *     "date_taken": 11234567
         * }
         */

        try {
            FileInputStream fileInputStream = new FileInputStream(pathFile);
            String fileName = new File(pathFile).getName();

            String boundary = "*****";

            // Open a HTTP connection to the URL

            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("PUT");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            // Ghi dữ liệu authen

            for (String key : infoAuth.keySet()) {
                writeFormData(dos, key, infoAuth.get(key));
            }

            // Ghi dữ liệu về file
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + "\r\n");
            dos.writeBytes("\r\n");

            // create a buffer of maximum size
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes("\r\n");
            dos.writeBytes("--*****--\r\n");
            // close streams
            fileInputStream.close();
            dos.flush();

            InputStream is = conn.getInputStream();

            int responseCode = conn.getResponseCode();
            Log.d("mc_log", "exePutFileConnection: responseCode - " + responseCode);
            String response = "";

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "{\"status\":0, \"msg\":\"unknown\"}";
            }
            Log.d("mc_log", "exePutFileConnection: response - " + response);

            dos.close();
            return response;
        } catch (MalformedURLException ex) {
            Log.e("mc_log", "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e("mc_log", "IO error: " + ioe.getMessage(), ioe);
        }
        Log.d("mc_log", "exePutFileConnection: response - " + "RONG");
        return "";
    }

    private static void writeFormData(DataOutputStream dos, String name, String value) throws IOException {
        dos.writeBytes("--*****\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + "\r\n");
        dos.writeBytes("\r\n");
        dos.writeBytes(value);
        dos.writeBytes("\r\n");
        dos.writeBytes("--*****\r\n");

    }


    private static String createUrlGet(String url, HashMap<String, String> parmas) {

        Uri.Builder uri = Uri.parse(url).buildUpon();

        Iterator iterator = parmas.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<String, String> pair = (Map.Entry<String, String>) iterator.next();
            uri.appendQueryParameter(pair.getKey(), pair.getValue());
//            url += "&" + pair.getKey() + "=" + pair.getValue();
        }
        return uri.build().toString();
    }

    private static JSONObject getJsonObjectFromMap(HashMap<String, Object> params) throws JSONException {
        Iterator iter = params.entrySet().iterator();
        JSONObject holder = new JSONObject();
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            String key = (String) pairs.getKey();
            HashMap m = (HashMap) pairs.getValue();
            JSONObject data = new JSONObject();
            Iterator iter2 = m.entrySet().iterator();
            while (iter2.hasNext()) {
                Map.Entry pairs2 = (Map.Entry) iter2.next();
                data.put((String) pairs2.getKey(), (String) pairs2.getValue());
            }
            holder.put(key, data);
        }
        return holder;
    }
}
