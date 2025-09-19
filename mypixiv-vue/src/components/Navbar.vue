<template>
  <nav
    class="navbar sticky-navbar"
    role="navigation"
    aria-label="main navigation"
  >
    <div class="navbar-brand">
      <a class="navbar-item" href="/">
        <img
          src="@/assets/images/Pixiv_Icon.svg"
          alt="logo"
          style="height: 48px"
        />
      </a>

      <a
        role="button"
        class="navbar-burger"
        aria-label="menu"
        aria-expanded="false"
        data-target="navbarBasicExample"
      >
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div id="navbarBasicExample" class="navbar-menu">
      <div class="navbar-start"></div>

      <div class="navbar-end">
        <div class="navbar-item">
          <div class="buttons">
            <a class="button is-primary">
              <strong>Sign up</strong>
            </a>
            <a class="button is-light"> Log in </a>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script></script>

<style scoped>
@import "../assets/css/sticky-navbar.css";

.sticky-navbar {
  background-color: #1e6fff;
  color: #ffffff;
}

.sticky-navbar .navbar-item,
.sticky-navbar .navbar-link,
.sticky-navbar .navbar-burger span {
  color: #ffffff;
}

.sticky-navbar .button.is-primary {
  background-color: #ffffff;
  color: #1e6fff;
  border: none;
}

.sticky-navbar .button.is-light {
  background-color: transparent;
  color: #ffffff;
  border: 1px solid rgba(255,255,255,0.2);
}
</style>
