const modal = document.querySelector(".modal");

document.querySelector(".timeline").addEventListener("click", (e) => {
  e.preventDefault();
  //   console.log(e.target);
  //   console.log(e.target.closest("div"));
  //   console.log(e.target.tagName);
  //   console.log(e.currentTarget);

  const eventTargetDiv = e.target.closest("div");

  if (eventTargetDiv.classList.contains("nextPostsCount")) {
    const nextPostsList = eventTargetDiv.nextElementSibling;

    if (nextPostsList.classList.contains("hidden")) {
      nextPostsList.classList.remove("hidden");
    } else {
      nextPostsList.classList.add("hidden");
    }
  } else if (eventTargetDiv.classList.contains("postImage")) {
    modal.classList.remove("hidden");
  } else if (e.target.tagName.toUpperCase() == "A") {
    console.log(e.target.href);
    // location.href = e.target.href;
  }
});

modal.addEventListener("click",(e)=>{
  if (e.target.classList.contains("modal") || e.target.classList.contains("modalBodyCloseBtn")) {
    modal.classList.add("hidden");
  }
})