export const extractErrorMessage = (error: unknown, fallback = '请求失败'): string => {
  if (!error) return fallback;
  const payload = error as any;

  if (typeof payload?.message === 'string' && typeof payload?.code === 'number') {
    return payload.message;
  }

  const responseMsg = payload?.response?.data?.message;
  if (typeof responseMsg === 'string' && responseMsg) {
    return responseMsg;
  }

  if (typeof payload?.message === 'string' && payload.message) {
    return payload.message;
  }

  if (typeof payload?.data?.message === 'string' && payload.data.message) {
    return payload.data.message;
  }

  return fallback;
};
