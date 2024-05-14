const aside = document.querySelector("aside");
aside.querySelector("button").addEventListener("click", (e) => {
  console.log(e.target);
  console.log(aside.classList);
  if (!aside.classList.contains("hidden")) {
    aside.classList.add("hidden");
    document.querySelector("main").style.maxWidth = "1920px";
    document.querySelector(".searchFormDiv").style.maxWidth = "1920px";
  }
});
