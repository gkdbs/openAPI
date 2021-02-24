package com.milkywaylhy.testopenapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Second extends AppCompatActivity {
    ArrayList<String> items = new ArrayList<String>();
    ListView listView;
    ArrayAdapter adapter;
    //API키
    String APIkey = "DLVCGYUTQNLKFMU&startPage=1&pageSize=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }

    public void clickBtn2(View view) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Date date = new Date();
                date.setTime((date.getTime()-(1000*60*60*24)));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String dateStr="20201228";
                String address = "http://210.99.248.79/rest/PublicParkingAreaService/getPublicParkingAreaList";

                try {

                    URL url = new URL(address);
                    InputStream is = url.openStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(isr);

                    int eventype = xpp.getEventType();

                    StringBuffer buffer = null;
                    while (eventype != XmlPullParser.END_DOCUMENT) {

                        switch (eventype) {
                            case XmlPullParser.START_DOCUMENT:
                                //별도의 Thread는 UI변경이 불가!!
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Second.this, "파싱 시작!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            case XmlPullParser.START_TAG:
                                String tagName = xpp.getName();
                                if (tagName.equals("list")) {
                                    buffer = new StringBuffer();
                                } else if (tagName.equals("idx")) {
                                    xpp.next();
                                    buffer.append("일련번호:" + xpp.getText() + "\n");

                                } else if (tagName.equals("kind")) {
                                    buffer.append("분류:");
                                    xpp.next();
                                    buffer.append(xpp.getText() + "\n");

                                } else if (tagName.equals("location")) {
                                    buffer.append("위치:");
                                    xpp.next();
                                    buffer.append(xpp.getText() + "\n");

                                } else if (tagName.equals("name")) {
                                    buffer.append("주차장명:");
                                    xpp.next();
                                    buffer.append(xpp.getText() + "\n");

                                }else if (tagName.equals("pcount")) {
                                    buffer.append("면수:");
                                    xpp.next();
                                    buffer.append(xpp.getText() + "\n");
                                }

                                break;

                            case XmlPullParser.TEXT:
                                break;

                            case XmlPullParser.END_TAG:
                                String tagName2 = xpp.getName();
                                if (tagName2.equals("dailyBoxOffice")) {
                                    //영화정보 항목1개가 종료...
                                    //그 때까지 StringBuffer에 append 한
                                    //데이터를 리스트뷰가 보여주는 ArrayList에 추가
                                    items.add(buffer.toString());
                                    //화면 변경은 별도 Thread가 할 수 없다!!
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                                }
                                break;

                        }//switch

                        eventype = xpp.next();
                    }//while


                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        };
        t.start();
    }
}