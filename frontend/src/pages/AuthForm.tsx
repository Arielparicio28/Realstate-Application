// src/pages/AuthForm.tsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Login from "../components/Login";
import Register from "../components/Register";
import Toggle from "../components/Toggle";

type AuthMode = "login" | "register";

interface AuthFormProps {
  mode: AuthMode;
}

const AuthForm: React.FC<AuthFormProps> = ({ mode }) => {
  const navigate = useNavigate();
  const [isLogin, setIsLogin] = useState(mode === "login");

  // Sincroniza si el usuario llega directamente vía URL
  useEffect(() => {
    setIsLogin(mode === "login");
  }, [mode]);

  const handleToggle = () => {
    if (isLogin) {
      navigate("/register");
    } else {
      navigate("/login");
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-4">
      <Toggle isLogin={isLogin} onToggle={handleToggle} />

      <div className="w-80 bg-white rounded-xl shadow-lg overflow-hidden perspective-1000">
        <div
          className={`
            relative w-full h-96 transform transition-transform duration-800
            [transform-style:preserve-3d]
            ${!isLogin ? "rotate-y-180" : ""}
          `}
        >
          <Login isVisible={isLogin} />
          <Register isVisible={!isLogin} />
        </div>
      </div>
    </div>
  );
};

export default AuthForm;
