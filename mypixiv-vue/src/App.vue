<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

/* reset default page margins so layout can use full viewport */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  overflow-x: hidden;
}

/* 确保只有一个滚动条 */
html {
  overflow-y: scroll;
}

body {
  overflow-y: auto;
}

#app {
  width: 100%;
  overflow-x: hidden;
}
</style>
