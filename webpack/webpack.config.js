module.exports = {
  entry: './src/main.jsx',
  output: {
    publicPath: '/readnow/',
    path: `${__dirname}/../docs`,
    filename: 'script.js',
  },
  resolve: {
    extensions: ['.js', '.jsx'],
  },
  devServer: {
    historyApiFallback: true,
    static: {
      directory: `${__dirname}/../docs`,
      publicPath: '/readnow/',
    },
  },
  mode: 'development',
  module: {
    rules: [
      {
        test: /\.jsx$/,
        loader: 'babel-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.css$/i,
        use: ["style-loader", "css-loader"],
      },
    ],
  },
};
