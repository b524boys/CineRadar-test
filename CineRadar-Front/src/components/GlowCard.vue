<template>
  <div class="card-container">
    <div class="card">
      <!-- 光晕层 -->
      <div class="glow glow-1"></div>
      <div class="glow glow-2"></div>
      <div class="glow glow-3"></div>

      <!-- 卡片内容 -->
      <div class="content">
<!--        <h1>Apple Intelligence</h1>
        <p>这是一个模仿Apple Intelligence风格的动态光晕卡片。卡片内的彩色光晕会缓慢移动，创造出有机且流畅的视觉效果。</p>
        <button class="action-button">了解更多</button>-->
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.card-container {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 15px;
}

.card {
  position: relative;
  padding: 10px;
  margin: 20px;
  background: rgba(255, 255, 255, 0.99);
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0px 0px 30px 4px rgba(255, 123, 37, 0.6);
  border-width: 1px;
  border-style: solid;
  border-color: rgba(255, 123, 37, 0.6);
}

.card:hover {
  cursor: pointer;
  box-shadow: 0 0 20px 2px rgba(248, 98, 255,0.4);
}

.content {
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  /*background: #f0f0f0;*/
  width: 180px;
  height: 307px;
  border-radius: 15px;
}

.action-button {
  background-color: #0071e3;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
  z-index: 9;
}

.glow {
  position: absolute;
  filter: blur(80px);
  opacity: 0.7;
  animation: glow-animation 2.5s infinite;
  border-radius: 50%;
  z-index: 0;
}

.glow-1 {
  background: conic-gradient(
      from 0deg at 50% 50%,
        /*rgba(255, 87, 34, 0.3),*/
      rgba(255, 193, 7, 1),
        /*rgba(156, 39, 176, 0.3),*/
      rgba(255, 102, 204, 1),
      rgb(42, 158, 255)
  );
  width: 600px;
  height: 600px;
  top: -50%;
  left: -30%;
  animation-direction: alternate;
}

.glow-2 {
  background: radial-gradient(
      circle at 50% 50%,
        /*rgba(51, 153, 255, 0.3),*/
      /*rgba(255, 193, 7, 1),*/
      rgb(255, 214, 7),
      rgba(255, 193, 7, 1)
    /*rgba(233, 30, 99, 0.3)*/
  );
  width: 400px;
  height: 400px;
  bottom: -30%;
  right: -20%;
  animation-delay: -2s;
}

.glow-3 {
  background: linear-gradient(
      45deg,
      rgba(255, 190, 0, 1),
      rgb(255, 48, 185),
      rgb(230, 72, 255)
  );
  width: 500px;
  height: 500px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -4s;
}

@keyframes glow-animation {
  0% {
    transform: rotate(0deg) scale(1);
  }
  50% {
    transform: rotate(180deg) scale(1.2);
  }
  100% {
    transform: rotate(360deg) scale(1);
  }
}

h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1d1d1f;
  z-index: 2;
}

p {
  font-size: 16px;
  line-height: 1.5;
  color: #424245;
  margin-bottom: 24px;
  max-width: 80%;
  z-index: 2;
}
</style>