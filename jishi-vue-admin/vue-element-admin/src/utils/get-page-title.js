import defaultSettings from '@/settings'

const title = defaultSettings.title || '' // 默认页面标题

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
