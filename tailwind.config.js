/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/main/webapp/**/*.html",
    "./src/main/webapp/**/*.jsp", // Also scan JSPs if Tailwind classes will be used there
    "./src/main/webapp/js/**/*.js", // If you use JS to manipulate classes
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
