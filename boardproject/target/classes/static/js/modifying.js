document.querySelector(".uploadResult").addEventListener("click", (e) => {
  e.preventDefault();

  const currentDiv = e.target.closest("div");
  if (e.target.classList.contains("imgRemoveBtn")) {
    if (confirm("정말로 삭제하시겠습니까?")) {
      currentDiv.remove();
    }
  }
});
