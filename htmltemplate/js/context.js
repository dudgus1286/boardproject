const modal = document.querySelector(".modal");

document.querySelector(".timelineContext").addEventListener("click", (e) => {
  e.preventDefault();
  //   console.log(e.target);
  //   console.log(e.target.closest("div"));
  //   console.log(e.target.tagName);
  //   console.log(e.currentTarget);

  const eventTargetDiv = e.target.closest("div");

  if (eventTargetDiv.classList.contains("nextPostsCount")) {
    const nextPostsList = document.querySelector(".nextPostsList");

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

document.querySelector(".modalBodyCloseBtn").addEventListener("click", (e) => {
  e.preventDefault();
  modal.classList.add("hidden");
});
