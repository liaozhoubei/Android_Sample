package com.example.example;

import com.example.example.retrofit.GnakApi;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
//https://www.jianshu.com/p/e906f7ee2a04
@RunWith(JUnit4.class)
public class WebTest {
    @Rule
    public final MockWebServer mockWebServer = new MockWebServer();
    private Service service;

    @Before
    public void setup() {
        service = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))  //实际项目中可使用真实url
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Service.class);
    }

    @Test
    public void testPOST() throws Exception {
        //模拟服务器的response
        mockWebServer.enqueue(new MockResponse().setBody("{\"name\": \"Tom\",\"age\": 100}"));

        Call<Person> call = service.modifyPerson("Tom");
        Person person = call.execute().body();

        assertNotNull(person);
        assertEquals("Tom", person.getName());
        assertEquals(100, person.getAge());
        assertEquals("POST", mockWebServer.takeRequest().getMethod());
    }

    @Test
    public void testGET_WithParam() throws Exception {
        mockWebServer.setDispatcher(dispatcher);   //dispatcher见下文
        Call<Person> call = service.fetchPersonById(5);
        Person person = call.execute().body();

        assertNotNull(person);
        assertEquals("A", person.getName());
        assertEquals(11, person.getAge());
        assertEquals("GET", mockWebServer.takeRequest().getMethod());
    }

    //dispatcher即为上文中的 mockWebServer.setDispatcher(dispatcher);
    private final Dispatcher dispatcher = new Dispatcher() {
        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            switch (request.getPath()) {
                case "/person/5":           //5即为GET请求fetchPersonById(@Path("id") int id)中的请求参数
                    return new MockResponse().setResponseCode(200).setBody("{\"name\": \"A\",\"age\": 11}");
                default:
                    return new MockResponse().setResponseCode(404);
            }
        }
    };
}
