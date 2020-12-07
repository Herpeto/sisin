const express = require('express')
const app = express() // server

var robot = require("robotjs"); // penggerak pointer
robot.setMouseDelay(2);

const requestLogger = (request, response, next) => {
  console.log('Method:', request.method)
  console.log('Path:  ', request.path)
  console.log('Body:  ', request.body)
  console.log('---')
  next()
}
app.use(requestLogger) // logger untuk debugging
app.use(express.json()) // parser body

app.get('/', (request, response) => { // tes koneksi
  response.send('<h1>Hello World!</h1>')
})

app.post('/api/move', (request, response) => { // menerima request gerakkan pointer
  const offset = request.body
  let pos = robot.getMousePos();
  robot.moveMouse(pos.x + offset.x, pos.y + offset.y);

  response.json(offset)
})

app.post('/api/leftclk', (request, response) => { // menerima request klik kiri
  robot.mouseClick("left");
  const success = {status:"success"}
  response.json(success)
})

app.post('/api/rightclk', (request, response) => { // menerima request klik kanan
  robot.mouseClick("right");
  const success = {status:"success"}
  response.json(success)
})

const PORT = 3001
app.listen(PORT, () => { // menunggu request di port 3001
  console.log(`Server running on port ${PORT}`)
})
