

type ButtonProps = {
  label: React.ReactNode;
  onClick: React.MouseEventHandler<HTMLButtonElement>;
  className?: string;
};

const Button = ({ label, onClick, className = "" }: ButtonProps) => {
  return (
    <button
      onClick={onClick}
      className={`w-1/2 py-2 rounded-md border-2 border-gray-600 shadow-lg active:translate-y-1 active:shadow-none font-semibold ${className}`}
    >
      {label}
    </button>
   
  )
}
export default Button;
