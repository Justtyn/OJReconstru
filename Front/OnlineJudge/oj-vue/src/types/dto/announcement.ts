export interface AnnouncementVO {
  id: string;
  title: string;
  content: string;
  time: string;
  isPinned: boolean;
  isActive: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface AnnouncementRequest {
  title: string;
  content: string;
  time?: string;
  isPinned?: boolean;
  isActive?: boolean;
}

export interface AnnouncementQuery {
  page?: number;
  size?: number;
  keyword?: string;
  pinnedOnly?: boolean;
  isPinned?: boolean;
}
