import { useState } from "react";
import { useNavigate} from "react-router-dom";
import Button from "./Button";
import { postLogin, postRegister } from "../services/auth";
import axios from "axios";

const Form = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [regUsername, setRegUsername] = useState("");
  const [regEmail, setRegEmail] = useState("");
  const [regPassword, setRegPassword] = useState("");

  const navigate = useNavigate();

  const handleToggle = () => setIsLogin(prev => !prev);

  const handleLogin = async () => {
    try {
      const response = await postLogin({
        emailOrUsername: loginEmail,
        password: loginPassword,
      });
      const { token } = response.data;
      console.log(axios);
      localStorage.setItem("token", token);
      // Redirige a pantalla principal
      navigate("/");
    } catch (error) {
  console.log(error);
    }
  };

  const handleRegister = async () => {
    try {
      const response = await postRegister({
        username: regUsername,
        email: regEmail,
        password: regPassword,
      });
        const { token } = response.data;
      localStorage.setItem("token", token);
      // Redirige a pantalla principal
      navigate("/");
      setIsLogin(true);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-4">
      {/* Toggle */}
      <div className="flex items-center space-x-3 mb-6">
        <span className="font-semibold text-gray-700">Log in</span>
        <div
          onClick={handleToggle}
          className={`w-12 h-6 rounded-full relative transition-colors duration-300 cursor-pointer ${
            isLogin ? 'bg-gray-300' : 'bg-blue-500'
          }`}
        >
          <div
            className={`absolute w-6 h-6 bg-white border-2 border-gray-600 rounded-full top-0.5 left-0.5 transition-transform duration-300 ${
              !isLogin ? 'translate-x-6' : ''
            }`}
          />
        </div>
        <span className="font-semibold text-gray-700">Sign up</span>
      </div>

      {/* Tarjeta */}
      <div className="w-80 bg-white rounded-xl shadow-lg overflow-hidden perspective-1000">
        <div
          className={`relative w-full h-96 transition-transform duration-800 transform-style-preserve-3d ${
            !isLogin ? 'rotate-y-180' : ''
          }`}
        >
          {/* Login */}
          <div className="absolute inset-0 backface-hidden flex flex-col items-center justify-center p-6">
            <h2 className="text-2xl font-bold mb-4 text-gray-800">Log in</h2>
            <input
              value={loginEmail}
              onChange={e => setLoginEmail(e.target.value)}
              className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
              placeholder="Username or Email"
            />
            <input
              value={loginPassword}
              onChange={e => setLoginPassword(e.target.value)}
              className="w-full mb-6 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
              type="password"
              placeholder="Password"
            />
            <Button label="Let’s go!" onClick={handleLogin} />
          </div>

          {/* Sign up */}
          <div className="absolute inset-0 rotate-y-180 backface-hidden flex flex-col items-center justify-center p-6">
            <h2 className="text-2xl font-bold mb-4 text-gray-800">Sign up</h2>
            <input
              value={regUsername}
              onChange={e => setRegUsername(e.target.value)}
              className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
              placeholder="Username"
            />
            <input
              value={regEmail}
              onChange={e => setRegEmail(e.target.value)}
              className="w-full mb-4 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
              placeholder="Email"
            />
            <input
              value={regPassword}
              onChange={e => setRegPassword(e.target.value)}
              className="w-full mb-6 p-2 border-2 border-gray-600 rounded-md focus:border-blue-500 outline-none"
              type="password"
              placeholder="Password"
            />
            <Button label="Confirm!" onClick={handleRegister} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Form;


