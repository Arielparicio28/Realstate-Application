import { Navigate, Outlet } from "react-router-dom";


/* ProtectedRoute: envuelve rutas que requieren token */
export function ProtectedRoute({ redirectPath = "/login"}) {
  const token = localStorage.getItem("token");
  const validToken = token !== null && token.trim() !== "";
  if (!validToken) {
  return <Navigate to={redirectPath} replace />;
}
  if (!token) return <Navigate to={redirectPath} replace />;
  return <Outlet />;
}
