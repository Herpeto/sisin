const express = require('express')
const app = express()

const requestLogger = (request, response, next) => {
  console.log('Method:', request.method)
  console.log('Path:  ', request.path)
  console.log('Body:  ', request.body)
  console.log('---')
  next()
}

app.use(requestLogger)


// Move the mouse across the screen as a sine wave.
var robot = require("robotjs");

// Speed up the mouse.
robot.setMouseDelay(2);






app.use(express.json());

app.get('/', (request, response) => {
  response.send('<h1>Hello World!</h1>')
})

app.post('/api/move', (request, response) => {
  const offset = request.body
  let pos = robot.getMousePos();
  robot.moveMouse(pos.x + offset.x, pos.y + offset.y);

  response.json(offset)
})


app.post('/api/leftclk', (request, response) => {
  robot.mouseClick("left");
  const success = {status:"success"}
  response.json(success)
})


app.post('/api/rightclk', (request, response) => {
  robot.mouseClick("right");
  const success = {status:"success"}
  response.json(success)
})

const PORT = 3001
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`)
})
