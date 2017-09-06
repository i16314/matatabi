package kosien.procon.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.uel_text);

        Button button = (Button) findViewById(R.id.url_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    new HttpGetTask().execute(new URL("http://150.15.103.157/php/sql2.php"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        Button b_button = (Button) findViewById(R.id.view_button);
        b_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    private final class HttpGetTask extends AsyncTask<URL, Void, String> {

        String st = null;
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(URL... urls) {
            // 取得したテキストを格納する変数aaa
            final StringBuilder result = new StringBuilder();
            // アクセス先URL
            final URL url = urls[0];

            HttpURLConnection con = null;
            try {
                // ローカル処理
                // コネクション取得
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                // HTTPレスポンスコード
                final int status = con.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    // 通信に成功した
                    // 本文の取得
                    InputStream in = con.getInputStream();
                    String readSt = readInputStream(in);

                    JSONObject jsonData = new JSONObject(readSt);
                    // String型の場合
                    st = jsonData.getString("station_name");
                    in.close();
                    Toast.makeText(MainActivity.this, st, Toast.LENGTH_LONG).show();
                }

            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            } finally {
                if (con != null) {
                    // コネクションを切断aa
                    con.disconnect();
                }
            }
            return result.toString();
        }

        String readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {
            StringBuffer sb = new StringBuffer();
            String str;

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while((str = br.readLine()) != null) {
                sb.append(str);
            }

            try {
                in.close();
            } catch(Exception e) {
                e.printStackTrace();
            }

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

        }
    }
}
