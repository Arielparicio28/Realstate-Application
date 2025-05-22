import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { postLogin } from "../services/auth";
import Button from "./Button";
import axios from "axios";

interface LoginProps {
  isVisible: boolean;
}

const Login: React.FC<LoginProps> = ({ isVisible }) => {
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");

  const navigate = useNavigate();

  const handleLogin = async () => {

    try {
      const response = await postLogin({
        emailOrUsername: loginEmail,
        password: loginPassword,
      });
      const { token } = response.data;
      localStorage.setItem("token", token);
      navigate("/");
    }catch (error: unknown) {
  
      if (axios.isAxiosError(error)) {
    console.error("Axios error:", error.response?.data);

    const message = error.response?.data?.message;
    if (message) {
      alert(message);
    }
  } else {
    console.error("Unknown error:", error);
    alert("Something went wrong.");
  }
    }
  };

  return (
    <div
      className={`
        absolute inset-0 backface-hidden flex flex-col items-center justify-center p-6
        ${!isVisible ? "hidden" : ""}
      `}
    >
      <h2 className="text-2xl font-bold mb-4 text-gray-800">Log in</h2>
      <input
        value={loginEmail}
        onChange={(e) => setLoginEmail(e.target.value)}
        className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
        placeholder="Username or Email"
      />

      <input
        value={loginPassword}
        onChange={(e) => setLoginPassword(e.target.value)}
        className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
        type="password"
        placeholder="Password"
      />
      <Button label="Let’s go!" onClick={handleLogin} />
    </div>
  );
};

export default Login;
