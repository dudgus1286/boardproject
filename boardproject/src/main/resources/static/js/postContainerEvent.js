// 포스트 이미지, 검색창 토글 관련 스크립트
// 관련변수
const postContainer = document.querySelector(".postContainer");
const modal = document.querySelector(".modal");

// 메인홈 타임라인
postContainer.addEventListener("click", (e) => {
  const closestDiv = e.target.closest("div");
  console.log(e.target);
  console.log(closestDiv);

  if (closestDiv.classList.contains("postImage")) {
    // 이미지 모달창 띄우기
    modal.classList.remove("hidden");
  } else if (closestDiv.classList.contains("nextPostsCount")) {
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
