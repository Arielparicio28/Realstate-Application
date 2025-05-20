
interface ToggleProps {
  isLogin: boolean;
  onToggle: () => void;
}

const Toggle: React.FC<ToggleProps> = ({ isLogin, onToggle }) => (
  <div className="flex items-center space-x-3 mb-6">
    <span className="font-semibold text-gray-700">Log in</span>
    <div
      onClick={onToggle}
      className={`
        w-12 h-6 rounded-full relative transition-colors duration-300 cursor-pointer
        ${isLogin ? "bg-gray-300" : "bg-blue-500"}
      `}
    >
      <div
        className={`
          absolute w-6 h-6 bg-white border-2 border-gray-600 rounded-full
          top-0.5 left-0.5 transition-transform duration-300
          ${!isLogin ? "translate-x-6" : ""}
        `}
      />
    </div>
    <span className="font-semibold text-gray-700">Sign up</span>
  </div>
);

export default Toggle;
