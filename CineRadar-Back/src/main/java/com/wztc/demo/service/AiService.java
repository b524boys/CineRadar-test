package com.wztc.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wztc.demo.entity.AiChat.AiResult;
import com.wztc.demo.entity.AiChat.ContentDto;
import com.wztc.demo.entity.AiChat.Movie;
import com.wztc.demo.entity.AiChat.NsAiResult;
import com.wztc.demo.entity.Goods;
import com.wztc.demo.entity.User;
import com.wztc.demo.mapper.GoodsMapper;
import com.wztc.demo.util.JsonUtils;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class AiService {

    private static final String DONE = "[DONE]";
    private static final Integer timeout = 60;
    private static final String MODEL = "deepseek-ai/DeepSeek-V3";
    private String liststring = null;

    @Autowired
    private GoodsMapper goodsMapper;

    @Value("${api.url:}")
    private String API_URL;

    @Value("${api.key:}")
    private String apiKey;

    /**
     * Ai聊天-流式输出
     */
    public void getAiResultStream(PrintWriter pw, String content, String Model) throws InterruptedException {
        //获取用户输入
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", content);
        //将用户输入构建messages.json
        List<Map> messages = new ArrayList<>();
        messages.add(message);
        //整合为请求体body.json
        Map<String, Object> params = new HashMap<>();
        params.put("model", Model);
        params.put("messages", messages);
        params.put("stream", true);
        String jsonParams = JsonUtils.convertObj2Json(params);
        //构建post请求&发送请求
        Request.Builder builder = new Request.Builder().url(API_URL);
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Authorization", " Bearer " + apiKey);
        RequestBody body = RequestBody.create(jsonParams, MediaType.parse("application/json; charset=utf-8"));
        Request request = builder.post(body).build();
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout,
                TimeUnit.SECONDS).build();
        //启动线程等待计时器
        CountDownLatch eventLatch = new CountDownLatch(1);
        // 实例化EventSource，注册EventSource监听器 -- 创建一个用于处理服务器发送事件的实例，并定义处理事件的回调逻辑
        RealEventSource realEventSource = new RealEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if (DONE.equals(data)) {
                    return;
                }
                String content = getContent(data);
                pw.write("data:" + JsonUtils.convertObj2Json(new ContentDto(content)) + "\n\n");
                pw.flush();
            }

            @Override
            public void onClosed(EventSource eventSource) {
                super.onClosed(eventSource);
                eventLatch.countDown();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                System.out.println(response.toString());
                System.out.printf("调用接口失败: %s%n", t);
                if (eventLatch != null) {
                    eventLatch.countDown();
                }
            }
        });
        // 与服务器建立连接
        realEventSource.connect(client);
        // await() 方法被调用来阻塞当前线程，直到 CountDownLatch 的计数变为0。
        eventLatch.await();
    }

    /**
     * 获取流式输出内容
     */
    private static String getContent(String data) {
        AiResult aiResult = JsonUtils.convertJson2Obj(data, AiResult.class);
        return aiResult.getChoices().get(0).getDelta().getContent();
    }

    /**
     * Ai推荐-非流式输出
     */
    public List<Movie> getAiRecMovieList(String content) throws InterruptedException {
        liststring = null;
        HttpServletRequest requestid = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User)requestid.getSession().getAttribute("user");
        int userId = user.getId();
        //提示词
        Map<String, String> prompt = new HashMap<>();
        prompt.put("role", "system");
        prompt.put("content", "你是一位电影推荐师,请根据用户输入的内容以及用户评分和收藏的电影列表,推荐六部用户最可能喜欢的电影,并为每部电影写出简短的推荐理由。" +
                "附加说明必须遵守: 1.推荐的电影必须是豆瓣Top250中的电影" +
                " 2.用户收藏电影列表:" + goodsMapper.getCollectedMovies(userId) +
                " 3.用户评分电影列表(电影名:分数值):" + goodsMapper.getRatedMovies(userId) +
                " 4.用户收藏电影均为用户喜欢看的电影,用户评分电影中,分数值在7.0-10.0的为用户喜欢看,0.0-4.5的为用户不喜欢看,4.5-6.9的为中性" +
                " 5.尽可能根据用户输入的内容做高度匹配的推荐,分析用户的情感,兴趣和电影的内容，并结合用户喜欢看的和不喜欢看的电影做综合推荐" +
                " 6.输出格式为JSON纯文本,包含:电影名称(必须是中文且不加书名号）,推荐理由(简短,不超过12个字)" +
                " 7.除了JSON之外,不要包含任何其他文本 5.输出内容不包含换行符(禁止输出Markdown格式)" +
                " 8.JSON案例为：[{\"name\":\"电影名称\",\"reason\":\"推荐理由\"},{\"name\":\"电影名称\",\"reason\":\"推荐理由\"}]");
        System.out.println(prompt.get("content"));
        //用户输入
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", content);
        //将提示词与用户输入一起构建messages.json
        List<Map> messages = new ArrayList<>();
        messages.add(prompt);
        messages.add(message);
        //整合为请求体body.json
        Map<String, Object> params = new HashMap<>();
        params.put("model", MODEL);
        params.put("messages", messages);
        params.put("stream", false);
        String jsonParams = JsonUtils.convertObj2Json(params);

        Request.Builder builder = new Request.Builder().url(API_URL);
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Authorization", " Bearer " + apiKey);
        RequestBody body = RequestBody.create(jsonParams, MediaType.parse("application/json; charset=utf-8"));
        Request request = builder.post(body).build();
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout,
                TimeUnit.SECONDS).build();

        CountDownLatch eventLatch = new CountDownLatch(1);
        //处理响应
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //e.printStackTrace();
                System.out.println("接口调用失败: " + e);
                if (eventLatch != null) {
                    eventLatch.countDown();
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.out.println("请求失败: " + response);
                    throw new IOException("Unexpected code " + response);
                } else {
                    // 处理响应数据
                    String responseData = response.body().string();
                    // 打印响应结果
                    System.out.println(responseData);
                    liststring = getNsContent(responseData);
//                    pw.write("data:" + JsonUtils.convertObj2Json(new ContentDto(content)) + "\n\n");
                }
                if (eventLatch != null) {
                    eventLatch.countDown();
                }
            }
        });
        // await() 方法被调用来阻塞当前线程，直到 CountDownLatch 的计数变为0。
        eventLatch.await();
        return JsonUtils.convertJsonArray2List(liststring, Movie.class);
    }

    /**
     * 获取非流式输出内容
     */
    private static String getNsContent(String data) {
        NsAiResult nsResult = JsonUtils.convertJson2Obj(data, NsAiResult.class);
        return nsResult.getChoices().get(0).getMessage().getContent();
    }

    /**
     * 处理LLM返回的电影列表（在本地数据库匹配和网络匹配）
     */
    public List<Movie> handleMovieList(List<Movie> prelist) throws InterruptedException {
        Iterator<Movie> iterator = prelist.iterator();
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).writeTimeout(timeout, TimeUnit.SECONDS).readTimeout(timeout,
                TimeUnit.SECONDS).build();
        while (iterator.hasNext()) {
            Movie movie = iterator.next();
            // 首先尝试从数据库查找
            Goods dbMovie = goodsMapper.selectByName(movie.getName());
            if (dbMovie != null) {
                // 如果数据库中存在，则赋值cover和id
                movie.setCover(dbMovie.getCover());
                movie.setUrl("http://localhost:5173/movieDetail?id=" + dbMovie.getId().toString());
            } else {
                // 如果数据库中不存在，则发送HTTP请求
                String url = "https://movie.douban.com/j/search_subjects?tag=" + movie.getName();
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    String responseBody = response.body().string();
                    System.out.println(responseBody);
                    // 如果请求失败，则移除当前Movie对象
                    if (response.isSuccessful() && !responseBody.equals("{\"subjects\":[]}")) {
                        System.out.println("豆瓣api查询到数据");
                        JSONObject jsonObject = JSON.parseObject(responseBody);
                        JSONArray subjects = jsonObject.getJSONArray("subjects");
                        if (subjects != null && subjects.size() > 0) {
                            JSONObject firstSubject = subjects.getJSONObject(0);
                            movie.setCover("https://images.weserv.nl/?url=" + firstSubject.getString("cover").replace("https://", ""));
                            movie.setUrl(firstSubject.getString("url"));
                        } else {
                            // 如果响应数据为空，则移除当前Movie对象
                            iterator.remove();
                        }
                    }else{
                        iterator.remove();
                    }
                } catch (IOException e) {
                    // 如果发生异常，则移除当前Movie对象
                    iterator.remove();
                    //e.printStackTrace();
                    System.out.println("电影搜索api请求失败: " + e);
                    return prelist;
                }
            }
        }
        return prelist;
    }

}
