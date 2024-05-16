const aside = document.querySelector("aside");
const menutoggle = document.querySelector(".menutoggle");
aside.querySelector("button").addEventListener("click", (e) => {
  console.log(e.target);
  console.log(aside.classList);
  if (!aside.classList.contains("hidden")) {
    aside.classList.add("hidden");
    document.querySelector("main").style.maxWidth = "1920px";
    document.querySelector(".searchFormDiv").style.maxWidth = "1920px";
    menutoggle.classList.remove("hidden");
  }
});

menutoggle.addEventListener("click", (e) => {
  menutoggle.classList.add("hidden");
  aside.classList.remove("hidden");
  document.querySelector("main").style.maxWidth = "1470px";
  document.querySelector(".searchFormDiv").style.maxWidth = "1470px";
});
