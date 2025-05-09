<template>
  <div class="chat-panel">
<!--    <div class="chat-title">EasyChatAI</div>-->
    <div class="message-panel" id="message-panel">
      <div class="message-list">
        <div
          :class="['message-item', item.type == 1 ? 'ai-item' : '']"
          v-for="(item, index) in messageList"
          :id="'item' + index"
        >
            <template v-if="item.type == 0">
              <div class="message-content">
                <div class="content-inner">{{ item.content }}</div>
              </div>
              <div class="user-icon">我</div>
            </template>
            <template v-else>
              <div class="user-icon">AI</div>
              <div class="ai-item">
                <MdPreview
                  previewTheme="vuepress"
                  :codeFoldable="false"
                  editorId="preview"
                  :modelValue="item.content.join('')"
                />
                <div class="loading" v-if="item.loading">
                  <img src="@/assets/imgs/front/loading.gif" />
                </div>
              </div>
            </template>
        </div>
      </div>
    </div>
    <div class="send-panel">
      <el-form :model="formData" ref="formDataRef" @submit.prevent>
        <!-- 下拉框 -->
        <el-form-item label="模型">
          <el-select clearable placeholder="选择模型" v-model="formData.model">
            <el-option value="deepseek-ai/DeepSeek-V3" label="DeepSeek-V3"></el-option>
            <el-option value="Qwen/Qwen2.5-7B-Instruct" label="通义千问-2.5"></el-option>
            <el-option value="THUDM/glm-4-9b-chat" label="智谱清言"></el-option>
          </el-select>
        </el-form-item>
        <!--input输入-->
        <el-form-item label="" prop="content">
          <el-input
            type="textarea"
            :rows="3"
            clearable
            placeholder="请输入你想问的问题"
            v-model="formData.content"
            @keyup="keySend"
          ></el-input>
        </el-form-item>
        <!--input输入-->
        <el-form-item label="" prop="" class="send-btn">
          <el-button type="primary" @click="sendMessage" :disabled="loading"
            >发送(ctrl+enter)</el-button
          >
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

const formData = ref({
  model: "deepseek-ai/DeepSeek-V3",
});
const messageList = ref([{
  type: '1',
  content: [
  '\n🎬 灯光渐暗，银幕亮起…\n' +
  '嗨，影迷朋友！🎥 我是AI电影小助手，有什么电影方面的问题可以和我畅所欲言呢。\n' +
  '\n' +
  '✨ 你可以这样解锁我：\n' +
  '▸ “你觉得肖申克的救赎有更深层的含义吗？”\n' +
  '▸ “《奥本海默》和《芭比》的联名梗怎么回事？”\n' +
  '▸ “用王家卫风格描述我今天的早餐…”\n' +
  '▸ “帮我吵赢！《流浪地球2》的机甲科学吗？”\n' +
  '\n' +
  '📽️ 小贴士：说出你的心情/类型/演员，我会更懂你～\n'
  ]
}]);
const loading = ref(false);

const keySend = (event) => {
  if (!(event.ctrlKey && event.key === "Enter")) {
    return;
  }
  sendMessage();
};

const sendMessage = () => {
  const message = formData.value.content;
  if (!message) {
    ElMessage({
      type: "warning",
      message: "请输入内容",
      duration: 2000,
    });
    return;
  }
  messageList.value.push({
    type: 0,
    content: message,
  });

  messageList.value.push({
    type: 1,
    content: [],
    loading: true,
  });
  loading.value = true;

  const eventSource = new EventSource(
      `/api/ai/chat?model=${formData.value.model}&message=${message}`
  );
  formData.value.content = "";
  eventSource.onmessage = (event) => {
    let response = event.data;
    console.log(response);
    if (response === "end") {
      close();
      return;
    }
    response = JSON.parse(response).content;
    messageList.value[messageList.value.length - 1].content.push(response);
    //滚动到底部
    nextTick(() => {
      const content = document.getElementById("message-panel");
      content.scrollTop = content.scrollHeight;
    });
  };

  eventSource.onerror = (error) => {
    close();
  };

  const close = () => {
    eventSource.close();
    messageList.value[messageList.value.length - 1].loading = false;
    loading.value = false;
  };
};
</script>

<style scoped>
.chat-panel {
 /* background: #eff0f6;*/
  width: 1000px;
  height: 100vh;
}

.chat-panel .chat-title {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}

.chat-panel .message-panel {
position: relative;
height: calc(100vh - 300px);
overflow: auto;
padding-bottom: 10px;
}

.chat-panel .message-panel .message-list {
margin: 0px auto;
width: 800px;
}

.chat-panel .message-panel .message-list .message-item {
margin: 10px 0px;
display: flex;
}

.chat-panel .message-panel .message-list .message-item .user-icon {
width: 40px;
height: 40px;
line-height: 40px;
border-radius: 20px;
background: #535353;
color: #fff;
text-align: center;
margin-left: 10px;
}

.chat-panel .message-panel .message-list .message-item .message-content {
  flex: 1;
 /* flex-grow: 0;   !* 允许元素拉伸以填充剩余空间 *!
  flex-shrink: 0;  !* 允许元素收缩以适应容器 *!
  flex-basis: 0%;  !* 初始尺寸为 0，分配空间完全由 flex-grow 决定 *!*/
  margin-left: 10px;
  align-items: center;
  display: flex;
  justify-content: flex-end;
}

.chat-panel .message-panel .message-list .message-item .content-inner {
background: #2d65f7;
border-radius: 12px;
padding: 10px;
color: #fff;
}

.chat-panel .message-panel .message-list .message-item .ai-item  {
  line-height: 23px;
  /*display: block;*/
  background: #fff;
  padding: 10px;
  border-radius: 12px;
  display: flex;
  margin-left: 10px;
  align-items: center;
}

.chat-panel .message-panel .message-list .ai-item .user-icon {
  flex-shrink: 0; /* 禁止压缩 */
  height: 40px; /* 固定高度 */
  width: 40px; /* 固定宽度 */
background: #ff804b;
margin-left: 0px;
}

.chat-panel .message-panel .message-list .ai-item .md-editor-previewOnly {
border-radius: 5px;
background: #fff;
}

.chat-panel .message-panel .message-list .ai-item .md-editor-preview-wrapper {
padding: 10px;
}

.chat-panel .message-panel .message-list .ai-item .loading {
text-align: center;
}

.chat-panel .send-panel {
position: relative;
margin: 5px auto 0px;
width: 800px;
background: #fff;
border-radius: 11px;
padding: 10px;
}

.chat-panel .send-panel .send-btn {
text-align: right;
margin-bottom: 0px;
padding: 5px;
}

.chat-panel .send-panel .send-btn .el-form-item__content {
justify-content: flex-end;
}

.chat-panel .send-panel .el-textarea__inner {
border: 0 !important;
resize: none !important;
box-shadow: none;
}

.no-data {
text-align: center;
color: #5f5f5f;
}
</style>


<!--<style lang="scss" scoped>
.chat-panel {
  background: #eff0f6;
  height: calc(100vh);
  .chat-title {
    text-align: center;
    font-size: 20px;
    font-weight: bold;
  }
  .message-panel {
    position: relative;
    height: calc(100vh - 234px);
    overflow: auto;
    padding-bottom: 10px;
    .message-list {
      margin: 0px auto;
      width: 800px;
      .message-item {
        margin: 10px 0px;
        display: flex;
        .user-icon {
          width: 40px;
          height: 40px;
          line-height: 40px;
          border-radius: 20px;
          background: #535353;
          color: #fff;
          text-align: center;
          margin-left: 10px;
        }
        .message-content {
          flex: 1;
          margin-left: 10px;
          align-items: center;
          display: flex;
          justify-content: flex-end;
        }
        .content-inner {
          background: #2d65f7;
          border-radius: 5px;
          padding: 10px;
          color: #fff;
        }
      }
      .ai-item {
        line-height: 23px;
        .message-content {
          display: block;
          background: #fff;
          border-radius: 5px;
        }
        .user-icon {
          background: #64018f;
          margin-left: 0px;
        }
        :deep(.md-editor-previewOnly) {
          border-radius: 5px;
          background: #fff;
        }
        :deep(.md-editor-preview-wrapper) {
          padding: 10px;
        }
        .loading {
          text-align: center;
        }
      }
    }
  }
  .send-panel {
    position: relative;
    margin: 5px auto 0px;
    width: 800px;
    background: #fff;
    border-radius: 5px;
    padding: 10px;
    .send-btn {
      text-align: right;
      margin-bottom: 0px;
      padding: 5px;
      :deep(.el-form-item__content) {
        justify-content: flex-end;
      }
    }
    :deep(.el-textarea__inner) {
      border: 0 !important;
      resize: none !important;
      box-shadow: none;
    }
  }
}
.no-data {
  text-align: center;
  color: #5f5f5f;
}
</style>-->
