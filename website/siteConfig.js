const repoUrl = "https://github.com/weso/utils";

const apiUrl = "/utils/api/weso/utils/index.html";

// See https://docusaurus.io/docs/site-config for available options.
const siteConfig = {
  title: "WESO Utils",
  tagline: "WESO Utils",
  url: "https://weso.github.io/utils",
  baseUrl: "/utils/",

  customDocsPath: "utils-docs/target/mdoc",

  projectName: "utils",
  organizationName: "weso",

  headerLinks: [
    { href: apiUrl, label: "API Docs" },
    { doc: "overview", label: "Documentation" },
    { href: repoUrl, label: "GitHub" }
  ],

  headerIcon: "img/fs2-kafka.white.svg",
  titleIcon: "img/fs2-kafka.svg",
  favicon: "img/favicon.png",

  colors: {
    primaryColor: "#122932",
    secondaryColor: "#153243"
  },

  copyright: `Copyright © 2018-${new Date().getFullYear()} OVO Energy Limited.`,

  highlight: { theme: "github" },

  onPageNav: "separate",

  separateCss: ["api"],

  cleanUrl: true,

  repoUrl,

  apiUrl
};

module.exports = siteConfig;
