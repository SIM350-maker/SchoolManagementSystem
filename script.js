// Select elements
const taskInput = document.getElementById("taskInput");
const taskDate = document.getElementById("taskDate");
const taskTime = document.getElementById("taskTime");
const addTaskButton = document.getElementById("addTaskButton");
const taskList = document.getElementById("taskList");
const currentDateTime = document.getElementById("currentDateTime");

// Function to update the current date and time
function updateCurrentDateTime() {
  const now = new Date();
  currentDateTime.textContent = now.toLocaleString();
}
setInterval(updateCurrentDateTime, 1000); // Update every second

// Add task function
addTaskButton.addEventListener("click", () => {
  const taskText = taskInput.value.trim();
  const taskDateValue = taskDate.value;
  const taskTimeValue = taskTime.value;

  if (taskText && taskDateValue && taskTimeValue) {
    const taskDateTime = new Date(`${taskDateValue}T${taskTimeValue}`);
    
    // Create a new list item
    const listItem = document.createElement("li");

    const taskInfo = document.createElement("span");
    taskInfo.textContent = `${taskText} (Due: ${taskDateTime.toLocaleString()})`;
    listItem.appendChild(taskInfo);

    // Add delete button
    const deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete";
    deleteButton.addEventListener("click", () => {
      taskList.removeChild(listItem);
    });
    listItem.appendChild(deleteButton);

    // Append the task to the list
    taskList.appendChild(listItem);

    // Clear inputs
    taskInput.value = "";
    taskDate.value = "";
    taskTime.value = "";

    // Sort tasks after adding
    sortTasks();
    updateTaskStyles();
  } else {
    alert("Please fill out all fields!");
  }
});

// Sort tasks by date and time
function sortTasks() {
  const tasks = Array.from(taskList.children);
  tasks.sort((a, b) => {
    const taskADate = new Date(a.querySelector("span").textContent.match(/\(Due: (.+?)\)/)[1]);
    const taskBDate = new Date(b.querySelector("span").textContent.match(/\(Due: (.+?)\)/)[1]);
    return taskADate - taskBDate;
  });
  taskList.innerHTML = "";
  tasks.forEach(task => taskList.appendChild(task));
}

// Highlight upcoming and overdue tasks
function updateTaskStyles() {
  const now = new Date();
  Array.from(taskList.children).forEach(task => {
    const taskDate = new Date(task.querySelector("span").textContent.match(/\(Due: (.+?)\)/)[1]);
    if (taskDate < now) {
      task.classList.add("overdue");
      task.classList.remove("upcoming");
    } else if (taskDate - now <= 3600000) { // Task is within the next hour
      task.classList.add("upcoming");
      task.classList.remove("overdue");
    } else {
      task.classList.remove("overdue", "upcoming");
    }
  });
}

// Periodically check and update task styles
setInterval(updateTaskStyles, 60000); // Every minute