// script.js
document.addEventListener("DOMContentLoaded", function () {
  console.log("博客系统前端脚本加载成功！");

  // 示例：可以添加一些客户端表单验证逻辑
  const loginForm = document.querySelector('.auth-form form[action*="/login"]');
  if (loginForm) {
    loginForm.addEventListener("submit", function (event) {
      const usernameInput = loginForm.querySelector("#username");
      const passwordInput = loginForm.querySelector("#password");

      if (!usernameInput.value.trim()) {
        alert("用户名不能为空！");
        event.preventDefault(); // 阻止表单提交
      }
      if (!passwordInput.value.trim()) {
        alert("密码不能为空！");
        event.preventDefault();
      }
    });
  }

  const commentForm = document.querySelector(".add-comment-form form");
  if (commentForm) {
    commentForm.addEventListener("submit", function (event) {
      const commentContent = commentForm.querySelector("#commentContent");
      if (!commentContent.value.trim()) {
        alert("评论内容不能为空！");
        event.preventDefault();
      }
    });
  }

  // 示例：为所有删除按钮添加确认提示
  document.querySelectorAll('form[onsubmit*="confirm"]').forEach((form) => {
    form.addEventListener("submit", function (event) {
      if (!confirm(this.getAttribute("onsubmit").match(/'([^']*)'/)[1])) {
        event.preventDefault();
      }
    });
  });
});
