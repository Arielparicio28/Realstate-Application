import { Navigate, Outlet } from "react-router-dom";


/* ProtectedRoute: envuelve rutas que requieren token */
export function ProtectedRoute({ redirectPath = "/login" }) {
  const token = localStorage.getItem("token");
  if (!token) return <Navigate to={redirectPath} replace />;
  return <Outlet />;
}