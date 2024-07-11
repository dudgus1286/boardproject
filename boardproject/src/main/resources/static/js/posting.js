document.querySelector(".uploadResult").addEventListener("click", (e) => {
  e.preventDefault();
  const currentDiv = e.target.closest("div");
  const filePath = e.target.closest("button").dataset.file;
  const formData = new FormData();
  formData.append("filePath", filePath);

  fetch("/upload/remove", {
    method: "post",
    headers: {
      "X-CSRF-TOKEN": csrfValue,
    },
    body: formData,
  })
    .then((response) => response.json())
    .then((data) => {
      if (data) {
        currentDiv.remove();
      }
    });
});
