<template>
  <div>
    <el-row :gutter="5" style="margin-bottom: 5px;">
      <!--第一列-->
      <el-col :span="24">
        <el-card :body-style="{ padding: '5px'}">
          <ul class="summary-container">
            <li class="summary-item">
              <span class="tip-icon"><el-icon><User /></el-icon></span>
              <div class="item-data">
                <span class="data-name" style="">用户数量</span>
                <h2 class="data-value" style="">{{ summaryData.userCount }}</h2>
              </div>
            </li>

            <li class="summary-item">
              <span class="tip-icon"><el-icon><DocumentCopy /></el-icon></span>
              <div class="item-data">
                <span class="data-name" style="">电影数量</span>
                <h2 class="data-value" style="">{{ summaryData.goodsCount }}</h2>
              </div>
            </li>

            <li class="summary-item">
              <span class="tip-icon"><el-icon><Money /></el-icon></span>
              <div class="item-data">
                <span class="data-name" style="">电影浏览量</span>
                <h2 class="data-value" style="">{{ summaryData.hitCount }}</h2>
              </div>
            </li>

            <li class="summary-item">
              <span class="tip-icon"><el-icon><ChatDotRound /></el-icon></span>
              <div class="item-data">
                <span class="data-name" style="">评论总数量</span>
                <h2 class="data-value" style="">{{ summaryData.commentsCount }}</h2>
              </div>
            </li>

          </ul>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="5" style="margin-bottom: 5px;">
      <!--用户注册-->
      <el-col :span="12">
        <el-card :body-style="{ padding: '5px'}">
          <div style="height: 50px;">
            <el-date-picker
                v-model="dateRange"
                value-format="yyyy-MM-dd"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期">
            </el-date-picker>
            <el-button type="primary" plain style="margin-left: 10px;" @click="loadUserRegister()">查询</el-button>
          </div>
          <div style="width: 100%; height: 300px;" id="userRegister"></div>
        </el-card>
      </el-col>
      <!--类型占比-->
      <el-col :span="12">
        <el-card :body-style="{ padding: '5px'}">
          <div style="width: 100%; height: 350px;" id="cateCount"></div>
        </el-card>
      </el-col>

    </el-row>

    <el-row :gutter="5" style="margin-bottom: 5px;">
      <el-col :span="12">
        <el-card :body-style="{ padding: '5px' }">
          <div style="width: 100%; height: 350px;" id="castCount"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card :body-style="{ padding: '5px'}">
          <div style="width: 100%; height: 350px;" id="yearCount"></div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts';
import requestAdmin from "@/utils/RequestAdmin.js";

// 统计数据
const summaryData = ref({})

// 时间范围
const dateRange = ref(null)

// ECharts 配置
//统计用户注册数据(折线图)
const userRegisterOption = {
  title: {
    text: '用户注册统计图'
  },
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: ['09-01', '09-02', '09-03', '09-04', '09-05', '09-06', '09-07']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '用户',
      type: 'line',
      smooth: false,
      data: [820, 1000, 901, 1200, 1290, 1330, 1320],
    }
  ]
};

//电影类型占比(饼图)
const cateCountOption = {
  title: {
    text: '电影类型占比饼图',
    left: 'center'
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: 'vertical',
    left: 'right'
  },

  series: [
    {
      name: '电影类型对应电影数占比',
      type: 'pie',
      radius: '60%',
      data: [
        { value: 888, name: '电影' },
        { value: 600, name: '电影' },
        { value: 1200, name: '电影' },
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
};

//演员出演电影数量
let castCountOption = {
  title: {
    text: '演员出演电影数量'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  legend: {},
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'value',
    boundaryGap: [0, 0.01]
  },
  yAxis: {
    type: 'category',
    data: ['Brazil', 'Indonesia', 'USA', 'India', 'China', 'World']
  },
  series: [
    {
      type: 'bar',
      data: [18203, 23489, 29034, 104970, 131744, 630230]
    }
  ]
};

//各年份电影上映数量
let yearCountOption = {
  title: {
    text: '各年份上映电影数量',
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross'
    }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    // prettier-ignore
    data: ['00:00', '01:15', '02:30', '03:45', '05:00', '06:15', '07:30', '08:45', '10:00', '11:15', '12:30', '13:45', '15:00', '16:15', '17:30', '18:45', '20:00', '21:15', '22:30', '23:45']
  },
  yAxis: {
    type: 'value',
  },
  series: [
    {
      name: 'Electricity',
      type: 'line',
      smooth: true,
      // prettier-ignore
      data: [300, 280, 250, 260, 270, 300, 550, 500, 400, 390, 380, 390, 400, 500, 600, 750, 800, 700, 600, 400],
    }
  ]
};

// 加载统计数据
const loadSummaryData = () => {
  requestAdmin.get('/chart/summary').then((res) => {
    summaryData.value = res?.data || {}
  })
}

// 加载用户注册数据
const loadUserRegister = () => {
  const userRegisterDom = document.getElementById('userRegister')
  const userRegisterChart = echarts.init(userRegisterDom)
  userRegisterChart.setOption(userRegisterOption)

  requestAdmin
      .get('/chart/userRegister', {
        params: {
          dateStart: dateRange.value ? dateRange.value[0] : '',
          dateEnd: dateRange.value ? dateRange.value[1] : '',
        },
      })
      .then((res) => {
        userRegisterOption.xAxis.data = res.data?.map((item) => item.name) || []
        userRegisterOption.series[0].data = res.data?.map((item) => item.value) || []
        userRegisterChart.setOption(userRegisterOption)
      })
}

// 加载电影类型占比数据
const loadCateCount = () => {
  const cateCountDom = document.getElementById('cateCount')
  const cateCountChart = echarts.init(cateCountDom)
  cateCountChart.setOption(cateCountOption)

  requestAdmin.get('/chart/cateCount').then((res) => {
    cateCountOption.series[0].data = res.data || []
    cateCountChart.setOption(cateCountOption)
  })
}

// 加载演员出演电影数量数据
const loadCastCount = () => {
  const castCountDom = document.getElementById('castCount')
  const castCountChart = echarts.init(castCountDom)
  castCountChart.setOption(castCountOption)

  requestAdmin.get('/chart/castCount').then((res) => {
    castCountOption.yAxis.data = res.data?.map((item) => item.name) || []
    castCountOption.series[0].data = res.data?.map((item) => item.value) || []
    castCountChart.setOption(castCountOption)
  })
}

// 加载各年份电影上映数量数据
const loadYearCount = () => {
  const yearCountDom = document.getElementById('yearCount')
  const yearCountChart = echarts.init(yearCountDom)
  yearCountChart.setOption(yearCountOption)

  requestAdmin.get('/chart/yearCount').then((res) => {
    yearCountOption.xAxis.data = res.data?.map((item) => item.name) || []
    yearCountOption.series[0].data = res.data?.map((item) => item.value) || []
    yearCountChart.setOption(yearCountOption)
  })
}

// 初始化加载数据
onMounted(() => {
  loadSummaryData()
  loadUserRegister()
  loadCateCount()
  loadCastCount()
  loadYearCount()
})
</script>

<style scoped>
@import '@/assets/css/manage/manage.css';
</style>