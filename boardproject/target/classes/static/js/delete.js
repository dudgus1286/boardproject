const actionForm = document.querySelector("#actionForm");

document.querySelector(".deleteBtnDiv").addEventListener("click", (e) => {
  if (e.target.classList.contains("readBtn")) {
    actionForm.action = "read#mainPost";
    actionForm.submit();
  } else if (e.target.classList.contains("deleteBtn")) {
    actionForm.submit();
  }
});
