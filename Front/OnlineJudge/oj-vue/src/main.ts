import { createApp } from 'vue';
import Antd from 'ant-design-vue';
import App from './App.vue';
import router from './router';
import { setupStore } from './stores';
import 'ant-design-vue/dist/reset.css';
import './styles/index.less';

const app = createApp(App);

setupStore(app);
app.use(Antd);
app.use(router);
app.mount('#app');
