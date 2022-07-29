<template>
  <div class="main-div">

    欢迎您，{{name}}
  </div>
</template>

<script>

import { mapGetters } from 'vuex'

export default {
  name: 'index',
  components: {},
  props: {
  },
  data() {
    return {
      username: ''
    }
  },
  created() {
  },
  methods: {},
  watch: {
  },
  computed: {
    ...mapGetters(["name"])
  },
  beforeMount() {
  },
  mounted() {
  }

}

</script>
<style lang="scss" scoped>
.main-div {
  display: inline-block;
  padding: 0 8px;
  height: 100%;
  font-size: 18px;
  color: #5a5e66;
  vertical-align: text-bottom;
}
</style>
