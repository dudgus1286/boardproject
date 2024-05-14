document.querySelector("#mainPost .postImages").addEventListener("click", (e) => {
  e.preventDefault();
  console.log("test");

  const eventTargetDiv = e.target.closest("div");

  if (eventTargetDiv.classList.contains("postImage")) {
    modal.classList.remove("hidden");
  } else if (e.target.tagName.toUpperCase() == "A") {
    console.log(e.target.href);
    // location.href = e.target.href;
  }
});
