import { marked } from 'marked';
import DOMPurify from 'dompurify';

marked.use({ gfm: true, breaks: true });

export const renderMarkdown = (content: string) => {
  const raw = marked.parse(content ?? '') as string;
  return DOMPurify.sanitize(raw);
};
