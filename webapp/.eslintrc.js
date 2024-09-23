module.exports = {
  env: {
    browser: true,
    es2021: true,
  },
  extends: ['airbnb-base', 'prettier'],
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
  },
  ignorePatterns: ["assets/", "src/Components/Pages"],
  rules: {
    'linebreak-style': 0,
    'no-use-before-define': ['error', 'nofunc'],
    "no-console": "off", // Ignorer les déclarations de console
  },
};
