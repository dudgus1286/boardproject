const fileInput = document.querySelector("#fileInput");
const uploadResult = document.querySelector(".uploadResult");

function checkExtension(fileName) {
  const regex = /(.*?).(png|gif|jpg)$/;
  return regex.test(fileName);
}

function showUploadImages(arr) {
  let tags = "";
  arr.forEach((obj) => {
    tags += `<div class="uploadPostImage" data-name="${obj.fileName}" data-path="${obj.folderPath}" data-uuid="${obj.uuid}">`;
    tags += `<img src="/upload/display?fileName=${obj.thumbImageURL}" alt=""/>`;
    tags += `<button class="imgRemoveBtn" data-file=${obj.imageURL}>지우기</button>`;
    tags += `<p>${obj.fileName}</p></div>`;
  });
  uploadResult.insertAdjacentHTML("beforeend", tags);
}

fileInput.addEventListener("change", (e) => {
  const files = e.target.files;
  const formData = new FormData();

  for (let index = 0; index < files.length; index++) {
    if (checkExtension(files[index].name)) {
      if (uploadResult.querySelectorAll(".uploadPostImage").length >= 4) {
        alert("한 게시물에 삽입하는 이미지는 4 개를 초과할 수 없습니다");
        return;
      }
      formData.append("uploadFiles", files[index]);
    }
  }

  fetch("/upload/uploadAjax", {
    method: "post",
    headers: {
      "X-CSRF-TOKEN": csrfValue,
    },
    body: formData,
  })
    .then((response) => response.json())
    .then((data) => {
      showUploadImages(data);
    });
});

const postingForm = document.querySelector("#postingForm");
postingForm.addEventListener("submit", (e) => {
  e.preventDefault();
  const form = e.target;

  const text = document.querySelector("#text");
  var pattern = /\s/g;
  if (text.value == "" || text.value.match(pattern)) {
    alert("텍스트는 비어둘 수 없습니다.");
    text.focus();
    return;
  }

  const attachInfos = document.querySelectorAll(".uploadResult .uploadPostImage");

  let result = "";
  attachInfos.forEach((obj, idx) => {
    result += `<input type="hidden" name="imageList[${idx}].path" value="${obj.dataset.path}">`;
    result += `<input type="hidden" name="imageList[${idx}].uuid" value="${obj.dataset.uuid}">`;
    result += `<input type="hidden" name="imageList[${idx}].imgName" value="${obj.dataset.name}">`;
  });
  form.insertAdjacentHTML("beforeend", result);
  form.submit();
});
