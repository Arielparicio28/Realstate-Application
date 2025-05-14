import { BrowserRouter, Route, Routes } from "react-router-dom"

import Home from "./pages/Home"
import { ProtectedRoute } from "./routes/ProtectedRoute"
import LoginAndRegister from "./pages/LoginAndRegister"

function App() {

  return (
    <>
      <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginAndRegister />} />
        <Route path="/register" element={<LoginAndRegister />} />
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<Home />} />
        </Route>
      </Routes>
    </BrowserRouter>
    </>
  )
}

export default App
