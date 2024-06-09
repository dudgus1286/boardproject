// 메뉴, 검색창 토글 관련 스크립트
// 관련 변수 설정
const aside = document.querySelector("aside");
const main = document.querySelector("main");
const footer = document.querySelector("footer");
const searchFormDiv = document.querySelector(".searchFormDiv");
const mainMenuToggle = document.querySelector("main button");

//
// 메뉴토글 버튼 클릭 이벤트
aside.querySelector("button").addEventListener("click", (e) => {
  e.preventDefault();
  aside.classList.add("hidden");
  mainMenuToggle.classList.remove("hidden");
  main.style.maxWidth = "1920px";
  footer.style.maxWidth = "1920px";
  searchFormDiv.style.maxWidth = "1920px";
  main.style.margin = "0 auto";
  footer.style.margin = "0 auto";
});

mainMenuToggle.addEventListener("click", (e) => {
  e.preventDefault();
  mainMenuToggle.classList.add("hidden");
  aside.classList.remove("hidden");
  main.style.maxWidth = "1620px";
  footer.style.maxWidth = "1620px";
  searchFormDiv.style.maxWidth = "1620px";
  main.style.margin = "";
  footer.style.margin = "";
});

// 사이트 방문했을 때 화면크기에 맞춰서 메뉴 여부
if (window.innerWidth <= 1742) {
  if (!aside.classList.contains("hidden")) {
    aside.classList.add("hidden");
    mainMenuToggle.classList.remove("hidden");
    main.style.maxWidth = "1920px";
    footer.style.maxWidth = "1920px";
    searchFormDiv.style.maxWidth = "1920px";
    main.style.margin = "0 auto";
    footer.style.margin = "0 auto";
  }
}

//
// 검색창 버튼 클릭 이벤트
searchFormDiv.querySelector(".searchtoggle").addEventListener("click", (e) => {
  e.preventDefault();
  searchFormDiv.classList.add("hidden");
  document.querySelector(".searchFormDiv + .searchtoggle").classList.remove("hidden");
  document.querySelector(".searchFormMargin").style.marginTop = "0";
});

document.querySelector(".searchFormDiv + .searchtoggle").addEventListener("click", (e) => {
  e.preventDefault();
  document.querySelector(".searchFormDiv + .searchtoggle").classList.add("hidden");
  searchFormDiv.classList.remove("hidden");
  document.querySelector(".searchFormMargin").style.marginTop = "150px";
});
