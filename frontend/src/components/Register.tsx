// src/components/Register.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { postRegister } from "../services/auth";
import Button from "./Button";

interface RegisterProps {
  isVisible: boolean;
}

const Register: React.FC<RegisterProps> = ({ isVisible }) => {
  const [regUsername, setRegUsername] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regPassword, setRegPassword] = useState("");
  const navigate = useNavigate();

  const handleRegister = async () => {
    try {
      const response = await postRegister({
        username: regUsername,
        email: regEmail,
        password: regPassword,
      });
      const { token } = response.data;
      localStorage.setItem("token", token);
      navigate("/");
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div
      className={`
        absolute inset-0 rotate-y-180 backface-hidden flex flex-col items-center justify-center p-6
        ${!isVisible ? "hidden" : ""}
      `}
    >
      <h2 className="text-2xl font-bold mb-4 text-gray-800">Sign up</h2>
      <input
        value={regUsername}
        onChange={(e) => setRegUsername(e.target.value)}
        className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
        placeholder="Username"
      />
      <input
        value={regEmail}
        onChange={(e) => setRegEmail(e.target.value)}
        className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
        placeholder="Email"
      />
      <input
        value={regPassword}
        onChange={(e) => setRegPassword(e.target.value)}
        className="w-full mb-6 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
        type="password"
        placeholder="Password"
      />
      <Button label="Confirm!" onClick={handleRegister} />
    </div>
  );
};

export default Register;
