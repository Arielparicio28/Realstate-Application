import { BrowserRouter, Route, Routes } from "react-router-dom";


import { ProtectedRoute } from "./routes/ProtectedRoute";
import AuthForm from "./pages/AuthForm";  
import DashBoard from "./pages/Dashboard";
function App() {
  return (
    <BrowserRouter>
    <Routes>
      {/* rutas abiertas */}
      <Route path="/login" element={<AuthForm mode="login" />} />
      <Route path="/register" element={<AuthForm mode="register" />} />

      {/* rutas protegidas */}
      <Route element={<ProtectedRoute />}>
        {/* SOLO estas rutas requieren token */}
        <Route path="/" element={<DashBoard />} />
      </Route>
    </Routes>
  </BrowserRouter>
  );
}

export default App;
