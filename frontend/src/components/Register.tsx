import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { postRegister } from "../services/auth";
import Button from "./Button";
import axios from "axios";

interface RegisterProps {
  isVisible: boolean;
}

const Register: React.FC<RegisterProps> = ({ isVisible }) => {
  const [regUsername, setRegUsername] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regPassword, setRegPassword] = useState("");

  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  const navigate = useNavigate();

  const validateInputs = () => {
    const newErrors: { [key: string]: string } = {};

    if (!regUsername || regUsername.length < 3) {
      newErrors.username = "Username must be at least 3 characters.";
    }

    if (!regEmail || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(regEmail)) {
      newErrors.email = "Invalid email address.";
    }

    if (!regPassword) {
      newErrors.password = "Password is required.";
    } else if (
      !/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{6,100}$/.test(regPassword)
    ) {
      newErrors.password =
        "Password must contain 1 uppercase, 1 lowercase, 1 digit, 1 special character, and be 6-100 characters long.";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async () => {
    if (!validateInputs()) return;

    try {
      const response = await postRegister({
        username: regUsername,
        email: regEmail,
        password: regPassword,
      });

      const { token } = response.data;
      localStorage.setItem("token", token);
      navigate("/login");
    }  catch (error: unknown) {
  
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
      {errors.username && (
        <p className="text-red-500 text-sm mb-2">{errors.username}</p>
      )}

      <input
        value={regEmail}
        onChange={(e) => setRegEmail(e.target.value)}
        className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
        placeholder="Email"
      />
      {errors.email && (
        <p className="text-red-500 text-sm mb-2">{errors.email}</p>
      )}

      <input
        value={regPassword}
        onChange={(e) => setRegPassword(e.target.value)}
        className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
        type="password"
        placeholder="Password"
      />
      {errors.password && (
        <p className="text-red-500 text-sm mb-2">{errors.password}</p>
      )}

      <Button label="Confirm!" onClick={handleRegister} />
    </div>
  );
};

export default Register;
