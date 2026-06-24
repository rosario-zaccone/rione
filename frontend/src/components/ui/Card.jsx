export function Card({ as: Component = "section", children, className = "", ...props }) {
  return (
    <Component className={`card ${className}`.trim()} {...props}>
      {children}
    </Component>
  );
}
