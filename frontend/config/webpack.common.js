
const webpack = require('webpack');
const helpers = require('./helpers');

/*
 * Webpack Plugins
 */
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ForkCheckerPlugin = require('awesome-typescript-loader').ForkCheckerPlugin;

/*
 * Webpack Constants
 */
const METADATA = {
  title: 'Allu Webpack',
  baseUrl: '/'
};

/*
 * Webpack configuration
 *
 * See: http://webpack.github.io/docs/configuration.html#cli
 */
module.exports = {
  /*
   * Cache generated modules and chunks to improve performance for multiple incremental builds.
   * This is enabled by default in watch mode.
   * You can pass false to disable it.
   *
   * See: http://webpack.github.io/docs/configuration.html#cache
   * cache: false,
   *
   * The entry point for the bundle
   * Our Angular.js app
   *
   * See: http://webpack.github.io/docs/configuration.html#entry
   */
  entry: {
    'vendor': './src/vendor.ts',
    'polyfills': './src/polyfills.ts',
    'app': './src/main.ts'
  },

  resolve: {
    /*
     * An array of extensions that should be used to resolve modules.
     *
     * See: http://webpack.github.io/docs/configuration.html#resolve-extensions
     */
    extensions: ['.ts', '.js'],

    modules: [
      helpers.root('src'),
      'node_modules'
    ],

    alias: {
      leafletcss: 'leaflet/dist/leaflet.css',
      leafletdrawcss: 'leaflet-draw/dist/leaflet.draw.css',
      leafletgroupedlayercontrolcss: 'leaflet-groupedlayercontrol/dist/leaflet.groupedlayercontrol.min.css',
      leafletmeasurepathcss: 'leaflet-measure-path/leaflet-measure-path.css',
      materializecss: helpers.root('src/assets/materialize/materialize.css'),
      materialize: helpers.root('src/assets/materialize/materialize.min.js'),
      filesaver: 'file-saver/FileSaver.js',
      finnishSsn: 'finnish-ssn/dist/finnish-ssn.min.js'
    }
  },

  /*
   * Options affecting the normal modules.
   *
   * See: http://webpack.github.io/docs/configuration.html#module
   */
  module: {

    noParse: [/[\/\\]node_modules[\/\\]proj4[\/\\]dist[\/\\]proj4\.js$/],

    rules: [
      /*
       * Preloaders
       */
      {
        test: /\.js$/,
        enforce: 'pre',
        loader: 'source-map-loader',
        exclude: [
          // these packages have problems with their sourcemaps
          helpers.root('node_modules/rxjs'),
          helpers.root('node_modules/@angular'),
          helpers.root('node_modules/angular2-jwt')
        ]
      },
      {
        test: /\.ts$/,
        enforce: 'pre',
        loader: 'tslint-loader',
        options: {
          emitErrors: false,
          failOnHint: true,
          resourcePath: 'src',
          tsConfigFile: helpers.root('tsconfig.json')
        },
        exclude: [
          // skip external modules
          helpers.root('node_modules')
        ]
      },

      /*
       * Normal Loaders
       */

      /*
       * Typescript loader support for .ts and Angular 2 async routes via .async.ts
       *
       * See: https://github.com/s-panferov/awesome-typescript-loader
       */
      {
        test: /\.ts$/,
        loader: 'awesome-typescript-loader',
        exclude: [/\.(spec|e2e)\.ts$/]
      },

      /*
       * Additional resource loader
       */
      {
        test: /.(woff(2)?|eot|ttf)(\?[a-z0-9=\.]+)?$/,
        loader: 'url-loader?limit=100000'
      },

      /*
       * Raw loader support for *.css files
       * Returns file content as string
       *
       * See: https://github.com/webpack/raw-loader
       */
      {
        test: /\.css$/,
        loader: 'style-loader!css-loader'
      },

      { test: /\.(png|jpg|svg)$/,
        loader: 'file-loader'
      },

      {
        test: /\.scss$/,
        exclude: /node_modules/,
        use: ['raw-loader','sass-loader']
      },

      /* Raw loader support for *.html
       * Returns file content as string
       *
       * See: https://github.com/webpack/raw-loader
       */
      {
        test: /\.html$/,
        use: 'raw-loader',
        exclude: [helpers.root('src/index.html')]
      }
    ]
  },

  /*
   * Add additional plugins to the compiler.
   *
   * See: http://webpack.github.io/docs/configuration.html#plugins
   */
  plugins: [

    /*
     * Plugin: CommonsChunkPlugin
     * Description: Shares common code between the pages.
     * It identifies common modules and put them into a commons chunk.
     *
     * See: https://webpack.github.io/docs/list-of-plugins.html#commonschunkplugin
     * See: https://github.com/webpack/docs/wiki/optimization#multi-page-app
     */
    new webpack.optimize.CommonsChunkPlugin({
      name: ['app', 'polyfills', 'vendor']
    }),

    /*
     * Plugin: CopyWebpackPlugin
     * Description: Copy files and directories in webpack.
     *
     * Copies project static assets.
     *
     * See: https://www.npmjs.com/package/copy-webpack-plugin
     */
    new CopyWebpackPlugin([{
      from: 'src/assets',
      to: 'assets'
    }]),

    /*
     * Plugin: HtmlWebpackPlugin
     * Description: Simplifies creation of HTML files to serve your webpack bundles.
     * This is especially useful for webpack bundles that include a hash in the filename
     * which changes every compilation.
     *
     * See: https://github.com/ampedandwired/html-webpack-plugin
     */
    new HtmlWebpackPlugin({
      template: 'src/index.html',
      title: METADATA.title,
      metadata: METADATA
    }),

    new webpack.ProvidePlugin({
      $: 'jquery',
      jQuery: 'jquery',
      'window.jQuery': 'jquery',
      Hammer: 'hammerjs/hammer'
    }),

    new webpack.ContextReplacementPlugin(
      /angular(\\|\/)core(\\|\/)@angular/,
      helpers.root('src'),
      {}
    )
  ],

  /*
   * Include polyfills or mocks for various node stuff
   * Description: Node configuration
   *
   * See: https://webpack.github.io/docs/configuration.html#node
   */
  node: {
    global: true,
    module: false,
    clearImmediate: false,
    setImmediate: false
  }
};
