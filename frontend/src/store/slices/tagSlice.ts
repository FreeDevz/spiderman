import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { tagService } from '../../services/tagService';
import type { Tag, CreateTagRequest, UpdateTagRequest } from '../../types';

// Async thunks
export const fetchTags = createAsyncThunk(
  'tags/fetchTags',
  async () => {
    const tags = await tagService.getTags();
    return tags;
  }
);

export const fetchTag = createAsyncThunk(
  'tags/fetchTag',
  async (id: number) => {
    const tag = await tagService.getTag(id);
    return tag;
  }
);

export const createTag = createAsyncThunk(
  'tags/createTag',
  async (tagData: CreateTagRequest) => {
    const tag = await tagService.createTag(tagData);
    return tag;
  }
);

export const updateTag = createAsyncThunk(
  'tags/updateTag',
  async ({ id, tagData }: { id: number; tagData: UpdateTagRequest }) => {
    const tag = await tagService.updateTag(id, tagData);
    return tag;
  }
);

export const deleteTag = createAsyncThunk(
  'tags/deleteTag',
  async (id: number) => {
    await tagService.deleteTag(id);
    return id;
  }
);

interface TagState {
  tags: Tag[];
  currentTag: Tag | null;
  loading: boolean;
  error: string | null;
}

const initialState: TagState = {
  tags: [],
  currentTag: null,
  loading: false,
  error: null,
};

const tagSlice = createSlice({
  name: 'tags',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setCurrentTag: (state, action) => {
      state.currentTag = action.payload;
    },
    clearCurrentTag: (state) => {
      state.currentTag = null;
    },
  },
  extraReducers: (builder) => {
    // Fetch tags
    builder
      .addCase(fetchTags.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTags.fulfilled, (state, action) => {
        state.loading = false;
        state.tags = action.payload;
      })
      .addCase(fetchTags.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch tags';
      });

    // Fetch single tag
    builder
      .addCase(fetchTag.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTag.fulfilled, (state, action) => {
        state.loading = false;
        state.currentTag = action.payload;
      })
      .addCase(fetchTag.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch tag';
      });

    // Create tag
    builder
      .addCase(createTag.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createTag.fulfilled, (state, action) => {
        state.loading = false;
        state.tags.push(action.payload);
      })
      .addCase(createTag.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to create tag';
      });

    // Update tag
    builder
      .addCase(updateTag.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateTag.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.tags.findIndex(tag => tag.id === action.payload.id);
        if (index !== -1) {
          state.tags[index] = action.payload;
        }
        if (state.currentTag?.id === action.payload.id) {
          state.currentTag = action.payload;
        }
      })
      .addCase(updateTag.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to update tag';
      });

    // Delete tag
    builder
      .addCase(deleteTag.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteTag.fulfilled, (state, action) => {
        state.loading = false;
        state.tags = state.tags.filter(tag => tag.id !== action.payload);
        if (state.currentTag?.id === action.payload) {
          state.currentTag = null;
        }
      })
      .addCase(deleteTag.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to delete tag';
      });
  },
});

export const {
  clearError,
  setCurrentTag,
  clearCurrentTag,
} = tagSlice.actions;

export default tagSlice.reducer; 