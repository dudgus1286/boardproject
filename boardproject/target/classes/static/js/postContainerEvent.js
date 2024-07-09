// 포스트 이미지, 검색창 토글 관련 스크립트
// 관련변수
const postContainer = document.querySelector(".postContainer");
const modal = document.querySelector(".modal");

// 메인홈 타임라인
postContainer.addEventListener("click", (e) => {
  const closestDiv = e.target.closest("div");
  // console.log(e.target);
  // console.log(closestDiv);

  if (closestDiv.classList.contains("postImage")) {
    // 이미지 모달창 띄우기
    modal.classList.remove("hidden");

    modal.querySelector(".modalBodyImage").innerHTML = `<img src="/upload/display?fileName=${closestDiv.getAttribute(
      "data-file"
    )}" alt="" onclick="window.open(this.src)"/>`;
    modal.querySelector(".writerNickname").innerHTML = `${closestDiv.getAttribute("data-writer")}`;

    // 원본글 링크
    let postLink = `<a href="/post/read?pno=${closestDiv.getAttribute("data-pno")}&page=${closestDiv.getAttribute("data-page")}`;
    if (closestDiv.getAttribute("data-type") && closestDiv.getAttribute("data-keyword")) {
      postLink += `&type=${closestDiv.getAttribute("data-type")}&keyword=${closestDiv.getAttribute("data-keyword")}#mainPost">원본글 링크</a>`;
    } else {
      postLink += `#mainPost">원본글 링크</a>`;
    }

    modal.querySelector(".postLink").innerHTML = postLink;
  } else if (closestDiv.classList.contains("nextPostsCount") || closestDiv.classList.contains("replyThreadToggle")) {
    // 댓글 토글
    const nextPostsList = closestDiv.nextElementSibling;

    if (nextPostsList.classList.contains("hidden")) {
      nextPostsList.classList.remove("hidden");
    } else {
      nextPostsList.classList.add("hidden");
    }
  }
});

// 이미지 모달 창
modal.addEventListener("click", (e) => {
  if (e.target.classList.contains("modal") || e.target.classList.contains("modalBodyCloseBtn")) {
    modal.classList.add("hidden");
  }
});
