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

  useEffect(() => {
    setIsLogin(mode === "login");
  }, [mode]);

  const handleToggle = () => {
    navigate(isLogin ? "/register" : "/login");
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-200 px-4 sm:px-6 py-8">
      <Toggle isLogin={isLogin} onToggle={handleToggle} />

      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl overflow-hidden perspective-1000 p-6 sm:p-8">
        <div
          className={`
            relative w-full h-[28rem] transform transition-transform duration-800
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
