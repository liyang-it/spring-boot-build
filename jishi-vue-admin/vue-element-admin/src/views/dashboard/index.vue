<template>
  <div class="dashboard-container">
    <!--    <component :is="currentRole" />-->
    <div style="display: none;">
      <h3>测试用， 上线需要删除</h3>
      <iframe
        src="http://localhost:9526/pms/druid"
        width="100%"
        scrolling="auto"
        frameborder="1"
        id="bi_iframe"
      ></iframe>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import adminDashboard from "./admin";
import editorDashboard from "./editor";

export default {
  name: "Dashboard",
  components: { adminDashboard, editorDashboard },
  data() {
    return {
      currentRole: "adminDashboard",
    };
  },
  computed: {
    ...mapGetters(["roles"]),
  },
  created() {
    if (!this.roles.includes("admin")) {
      this.currentRole = "editorDashboard";
    }
  },
  mounted() {
    document.getElementById("bi_iframe").height =
      document.documentElement.clientHeight - 100;
  },
};
</script>
